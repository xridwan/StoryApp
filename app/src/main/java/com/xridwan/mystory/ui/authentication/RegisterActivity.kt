package com.xridwan.mystory.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.xridwan.mystory.databinding.ActivityRegisterBinding
import com.xridwan.mystory.network.ApiConfig
import com.xridwan.mystory.response.RegisterResponse
import com.xridwan.mystory.ui.utils.hide
import com.xridwan.mystory.ui.utils.show
import com.xridwan.mystory.ui.utils.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()

        binding.btnRegister.setOnClickListener {
            validate()
        }
    }

    private fun setView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        ObjectAnimator.ofFloat(binding.imageView2, View.TRANSLATION_Y, -15F, 15F).apply {
            duration = 5000
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.textView4, View.ALPHA, 1f).setDuration(250)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailTextInputLayout, View.ALPHA, 1f).setDuration(250)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordTextInputLayout, View.ALPHA, 1f).setDuration(250)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameTextInputLayout, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title, nameEditTextLayout, emailEditTextLayout, passwordEditTextLayout, register
            )
            startDelay = 500
        }.start()
    }

    private fun validate() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val name = binding.etName.text.toString()

        when {
            name.isEmpty() -> {
                binding.etName.error = "Input Your Name"
                binding.etName.requestFocus()
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Input Your Email"
                binding.etEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Input your Password"
                binding.etPassword.requestFocus()
            }
            else -> {
                register()
            }
        }
    }

    private fun register() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        binding.progressCircular.show()

        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful) {
                    showToast("${responseBody?.message}")
                    finish()
                } else {
                    showToast("${responseBody?.message}")
                }
                binding.progressCircular.hide()
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showToast("${t.message}")
                binding.progressCircular.hide()
            }
        })
    }
}