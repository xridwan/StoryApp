package com.xridwan.mystory.ui.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.xridwan.mystory.ViewModelFactory
import com.xridwan.mystory.databinding.ActivityAddBinding
import com.xridwan.mystory.datastore.UserPreferences
import com.xridwan.mystory.network.ApiConfig
import com.xridwan.mystory.response.FileUploadResponse
import com.xridwan.mystory.ui.camera.CameraActivity
import com.xridwan.mystory.ui.main.MainActivity
import com.xridwan.mystory.ui.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var viewModel: AddViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        if (!permissionGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PREMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener {
            startCameraX()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            viewModel.getToken().observe(this) { token ->
                postImage(token)
            }
        }
    }

    private fun postImage(token: String?) {
        val userToken = "Bearer $token"

        binding.progressCircular.show()

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.tvDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val multiForm = "application/json"

            val service = ApiConfig.getApiService()
                .postImage(imageMultipart, description, userToken, multiForm)
            service.enqueue(object : Callback<FileUploadResponse> {
                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            showToast(responseBody.message)
                            sendIntent()
                        }
                    } else {
                        showToast(response.message())
                    }
                    binding.progressCircular.hide()
                }

                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    showToast("Failed to load data")
                    binding.progressCircular.hide()
                }
            })
        } else {
            showToast("Please get your image")
            binding.progressCircular.hide()
        }
    }

    private fun sendIntent() {
        startActivity(Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[AddViewModel::class.java]
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            val mFile = uriToFile(selectedImage, this)
            getFile = mFile
            binding.ivPreview.setImageURI(selectedImage)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val mFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = mFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path), isBackCamera
            )

            binding.ivPreview.setImageBitmap(result)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!permissionGranted()) {
                Toast.makeText(this, "Can't Get Permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun permissionGranted() = REQUIRED_PREMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PREMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 100
    }
}