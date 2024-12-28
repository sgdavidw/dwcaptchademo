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
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            cacheMode = WebSettings.LOAD_NO_CACHE
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        // Enable WebView debugging
        WebView.setWebContentsDebuggingEnabled(true)

        webView.visibility = View.INVISIBLE
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "CAPTCHA WebView started loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "CAPTCHA WebView finished loading: $url")
                view?.visibility = View.VISIBLE
                // Log WebView content height
                view?.let {
                    Log.d(TAG, "WebView content height: ${it.contentHeight}")
                    Log.d(TAG, "WebView layout height: ${it.layoutParams.height}")
                }
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e(TAG, "CAPTCHA WebView error: $errorCode - $description, URL: $failingUrl")
                runOnUiThread {
                    showError("WebView error: $description")
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d(TAG, "CAPTCHA WebView shouldOverrideUrlLoading: $url")
                return false  // Let the WebView handle all URLs
            }
        }

        // Add console message logging
        webView.setWebChromeClient(object : android.webkit.WebChromeClient() {
            override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
                Log.d(TAG, "WebView Console: ${message.message()} -- From line ${message.lineNumber()} of ${message.sourceId()}")
                return true
            }
        })

        Log.d(TAG, "WebView setup completed with all settings enabled")
    }

    private fun setupCaptcha() {
        try {
            // Define the privacy policy implementation
            val privacyPolicy = object : TencentCaptchaConfig.ITencentCaptchaPrivacyPolicy {
                override fun userAgreement(): Boolean {
                    Log.d(TAG, "Privacy policy check called")
                    return true
                }
            }

            // Define the device info provider implementation
            val deviceInfoProvider = object : TencentCaptchaConfig.ICaptchaDeviceInfoProvider {
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
            val captchaWebView = binding.root.findViewById<WebView>(R.id.tcaptchaWebview)
            
            // Set WebView height to accommodate the slide verification
            captchaWebView.layoutParams = captchaWebView.layoutParams.apply {
                height = resources.displayMetrics.heightPixels / 3  // Adjust height to 1/3 of screen
            }
            Log.d(TAG, "Set WebView height to: ${captchaWebView.layoutParams.height}")
            
            // Ensure WebView is visible from the start
            captchaWebView.visibility = View.VISIBLE
            Log.d(TAG, "WebView visibility set to VISIBLE")

            val captchaParamBuilder = TencentCaptchaParam.Builder()
                .setWebView(captchaWebView)
                .setCaptchaAppid("189992641")
                .setType("popup")  // Change to popup mode
                .setEnableDarkMode(false)
                .setLoading(true)
                .setUserLanguage("en")
                .setNeedFeedBack(true)
                .setEnableAged(true)
                .setOptionsCallback(object : TencentCaptchaParam.OptionsCallback {
                    override fun onCallback(optFuncName: String, data: String) {
                        Log.d(TAG, "CAPTCHA options callback: $optFuncName, data: $data")
                        if (optFuncName == "ready") {
                            Log.d(TAG, "CAPTCHA dimensions: $data")
                            runOnUiThread {
                                // Ensure WebView stays visible
                                captchaWebView.visibility = View.VISIBLE
                            }
                        } else if (optFuncName == "showFn") {
                            Log.d(TAG, "CAPTCHA show function called: $data")
                            runOnUiThread {
                                // Ensure WebView stays visible when showing CAPTCHA
                                captchaWebView.visibility = View.VISIBLE
                            }
                        }
                    }
                })

            Log.d(TAG, "CAPTCHA parameters configured with App ID: 189992641")

            val captchaCallback = object : TencentCaptchaCallback {
                override fun finish(ret: RetCode, resultObject: JSONObject?) {
                    Log.d(TAG, "CAPTCHA callback received: ${ret.code}, ${resultObject?.toString()}")
                    Log.d(TAG, "CAPTCHA return code description: ${ret.msg}")
                    
                    // Add a delay before processing the result
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        runOnUiThread {
                            if (ret == RetCode.OK && resultObject?.has("ticket") == true) {
                                Log.d(TAG, "CAPTCHA passed successfully with ticket")
                                captchaWebView.visibility = View.GONE
                                viewModel.login(email, password)
                            } else {
                                Log.e(TAG, "CAPTCHA failed or no ticket: ${ret.code}, ${ret.msg}")
                                showError("Please complete the verification")
                                // Keep WebView visible for retry
                                captchaWebView.visibility = View.VISIBLE
                            }
                        }
                    }, 1000) // 1 second delay
                }

                override fun exception(t: Throwable) {
                    Log.e(TAG, "CAPTCHA exception: ${t.message}", t)
                    Log.e(TAG, "CAPTCHA exception stack trace", t)
                    runOnUiThread {
                        showError("An error occurred during verification. Please try again.")
                        // Keep WebView visible on error
                        captchaWebView.visibility = View.VISIBLE
                    }
                }
            }

            val startRet = TencentCaptcha.start(captchaCallback, captchaParamBuilder.build())
            Log.d(TAG, "CAPTCHA start result code: ${startRet.code}")
            Log.d(TAG, "CAPTCHA start result message: ${startRet.msg}")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting CAPTCHA: ${e.message}")
            Log.e(TAG, "CAPTCHA error stack trace", e)
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