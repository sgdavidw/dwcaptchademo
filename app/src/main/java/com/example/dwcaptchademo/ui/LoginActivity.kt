package com.example.dwcaptchademo.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dwcaptchademo.R
import com.example.dwcaptchademo.databinding.ActivityLoginBinding
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.utils.ITencentCaptchaPrivacyPolicy
import com.example.dwcaptchademo.utils.ICaptchaDeviceInfoProvider
import com.example.dwcaptchademo.viewmodels.LoginViewModel
import com.tencent.captcha.sdk.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.isLoggedIn()) {
            startProfileActivity()
            finish()
            return
        }

        setupWebView()
        setupCaptcha()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupWebView() {
        val webView = binding.root.findViewById<WebView>(R.id.tcaptchaWebview)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            databaseEnabled = true
            setGeolocationEnabled(true)
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.webViewClient = WebViewClient()
        Log.d(TAG, "WebView setup completed")
    }

    private fun setupCaptcha() {
        try {
            // Define the privacy policy implementation
            val privacyPolicy = object : ITencentCaptchaPrivacyPolicy {
                override fun userAgreement(): Boolean {
                    Log.d(TAG, "Privacy policy check called")
                    return true
                }
            }

            // Define the device info provider implementation
            val deviceInfoProvider = object : ICaptchaDeviceInfoProvider {
                override fun getAndroidId(): String {
                    val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    Log.d(TAG, "Device ID provided: $androidId")
                    return androidId
                }
            }

            // Initialize the CAPTCHA SDK
            val configBuilder = TencentCaptchaConfig.Builder(applicationContext, privacyPolicy, deviceInfoProvider)
            val ret = TencentCaptcha.init(configBuilder.build())
            if (ret != RetCode.OK) {
                Log.e(TAG, "Initialization failed: ${ret.code}, ${ret.msg}")
                showError("CAPTCHA initialization failed")
                return
            }
            Log.d(TAG, "CAPTCHA SDK initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up CAPTCHA: ${e.message}", e)
            showError("Error setting up CAPTCHA")
        }
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isBlank() || password.isBlank()) {
                showError("Please enter email and password")
                return@setOnClickListener
            }

            // Trigger CAPTCHA before login
            startCaptchaVerification(email, password)
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun startCaptchaVerification(email: String, password: String) {
        try {
            Log.d(TAG, "Starting CAPTCHA verification")
            // Set up the CAPTCHA WebView
            val captchaWebView = binding.root.findViewById<WebView>(R.id.tcaptchaWebview)
            captchaWebView.visibility = View.VISIBLE

            // Configure the CAPTCHA parameters
            val captchaParamBuilder = TencentCaptchaParam.Builder()
                .setWebView(captchaWebView)
                .setCaptchaAppid("189992641")

            // Define the CAPTCHA callback
            val captchaCallback = object : TencentCaptchaCallback {
                override fun finish(ret: RetCode, resultObject: JSONObject?) {
                    Log.d(TAG, "CAPTCHA callback received: ${ret.code}, ${resultObject?.toString()}")
                    runOnUiThread {
                        captchaWebView.visibility = View.GONE
                        if (ret == RetCode.OK) {
                            Log.d(TAG, "CAPTCHA passed successfully")
                            viewModel.login(email, password)
                        } else {
                            Log.e(TAG, "CAPTCHA failed: ${ret.code}, ${ret.msg}")
                            showError("CAPTCHA verification failed. Please try again.")
                        }
                    }
                }

                override fun exception(t: Throwable) {
                    Log.e(TAG, "CAPTCHA exception", t)
                    runOnUiThread {
                        captchaWebView.visibility = View.GONE
                        showError("An error occurred during CAPTCHA verification.")
                    }
                }
            }

            // Start the CAPTCHA verification
            val startRet = TencentCaptcha.start(captchaCallback, captchaParamBuilder.build())
            Log.d(TAG, "CAPTCHA start result: $startRet")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting CAPTCHA", e)
            showError("Error starting CAPTCHA verification")
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Log.d(TAG, "Login successful")
                    showLoading(false)
                    startProfileActivity()
                    finish()
                }
                is Resource.Error -> {
                    Log.e(TAG, "Login error: ${result.message}")
                    showLoading(false)
                    showError(result.message)
                }
                is Resource.Loading -> {
                    Log.d(TAG, "Login loading")
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
        Log.e(TAG, "Showing error: $message")
        binding.errorText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun startProfileActivity() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}