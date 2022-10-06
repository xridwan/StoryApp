package com.xridwan.mystory.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.xridwan.mystory.ViewModelFactory
import com.xridwan.mystory.databinding.ActivityLoginBinding
import com.xridwan.mystory.datastore.UserPreferences
import com.xridwan.mystory.network.ApiConfig
import com.xridwan.mystory.response.LoginResponse
import com.xridwan.mystory.ui.main.MainActivity
import com.xridwan.mystory.ui.utils.hide
import com.xridwan.mystory.ui.utils.show
import com.xridwan.mystory.ui.utils.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setupViewModel()
        getSession()

        binding.btnLogin.setOnClickListener {
            validate()
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[AuthViewModel::class.java]
    }

    private fun getSession() {
        viewModel.isLogin().observe(this) { session ->
            if (session) {
                binding.progressCircular.show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            binding.progressCircular.hide()
        }
    }

    private fun validate() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.etEmail.error = "Input Your Email"
                binding.etEmail.requestFocus()
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Input Your Password"
                binding.etPassword.requestFocus()
            }
            else -> {
                login()
            }
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        binding.progressCircular.show()

        val service = ApiConfig.getApiService().login(email, password)
        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful) {
                    responseBody?.loginResult?.token?.let {
                        viewModel.saveToken(it)
                    }
                    viewModel.login()
                    showToast("${responseBody?.message}")
                } else {
                    showToast("${responseBody?.message}")
                }
                binding.progressCircular.hide()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showToast("${t.message}")
                binding.progressCircular.hide()
            }
        })
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

        ObjectAnimator.ofFloat(binding.loginImage, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 5000
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(250)
        val subTitle = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(250)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailTextInputLayout, View.ALPHA, 1f).setDuration(250)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordTextInputLayout, View.ALPHA, 1f).setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title,
                subTitle,
                emailEditTextLayout,
                passwordEditTextLayout,
                login,
                register
            )
            startDelay = 500
        }.start()
    }
}