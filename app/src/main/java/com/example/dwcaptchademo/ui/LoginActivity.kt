package com.example.dwcaptchademo.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dwcaptchademo.R
import com.example.dwcaptchademo.databinding.ActivityLoginBinding
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.viewmodels.LoginViewModel
import com.tencent.captcha.sdk.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.isLoggedIn()) {
            startProfileActivity()
            finish()
            return
        }

        setupCaptcha()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupCaptcha() {
        // Define the privacy policy implementation
        val privacyPolicy = object : ITencentCaptchaPrivacyPolicy {
            override fun userAgreement(): Boolean {
                // Return true if the user has agreed to the privacy policy
                return true
            }
        }

        // Define the device info provider implementation
        val deviceInfoProvider = object : ICaptchaDeviceInfoProvider {
            override fun getAndroidId(): String {
                // Return the device's Android ID
                return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            }
        }

        // Initialize the CAPTCHA SDK
        val configBuilder = TencentCaptchaConfig.Builder(applicationContext, privacyPolicy, deviceInfoProvider)
        val ret = TencentCaptcha.init(configBuilder.build())
        if (ret != RetCode.OK) {
            Log.e("TencentCaptcha", "Initialization failed: ${ret.code}, ${ret.msg}")
            return
        }
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            // Trigger CAPTCHA before login
            startCaptchaVerification(email, password)
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun startCaptchaVerification(email: String, password: String) {
        // Set up the CAPTCHA WebView
        val captchaWebView = binding.root.findViewById<WebView>(R.id.tcaptchaWebview)

        // Configure the CAPTCHA parameters
        val captchaParamBuilder = TencentCaptchaParam.Builder()
            .setWebView(captchaWebView) // Bind the CAPTCHA WebView
            .setCaptchaAppid("189992641") // Replace with your CAPTCHA App ID

        // Define the CAPTCHA callback
        val captchaCallback = object : TencentCaptchaCallback {
            override fun finish(ret: RetCode, resultObject: org.json.JSONObject?) {
                if (ret == RetCode.OK) {
                    Log.d("TencentCaptcha", "CAPTCHA passed successfully.")
                    viewModel.login(email, password) // Proceed with login
                } else {
                    Log.e("TencentCaptcha", "CAPTCHA failed: ${ret.code}, ${ret.msg}")
                    showError("CAPTCHA verification failed. Please try again.")
                }
            }

            override fun exception(t: Throwable) {
                Log.e("TencentCaptcha", "CAPTCHA exception: ${t.message}")
                showError("An error occurred during CAPTCHA verification.")
            }
        }

        // Start the CAPTCHA verification
        TencentCaptcha.start(captchaCallback, captchaParamBuilder.build())
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    startProfileActivity()
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message)
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.emailInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        binding.errorText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun startProfileActivity() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}