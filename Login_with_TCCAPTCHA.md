# Integrating Tencent CAPTCHA Native SDK into Android Login Page

This guide explains how to integrate Tencent CAPTCHA Native SDK into an Android login page for enhanced security.

## Prerequisites

1. Android Studio Arctic Fox or later
2. Android SDK with minimum API level 28
3. Tencent CAPTCHA App ID (obtain from Tencent Cloud Console)
4. Tencent CAPTCHA Native SDK AAR file (`Android.aar`)

## Step 1: Project Setup

1. Copy the SDK AAR file to your project:
   ```bash
   mkdir -p app/libs
   cp path/to/Android.aar app/libs/
   ```

2. Add AAR dependency in `app/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
   }
   ```

3. Configure NDK in `app/build.gradle.kts`:
   ```kotlin
   android {
       defaultConfig {
           ndk {
               abiFilters += listOf("armeabi-v7a", "arm64-v8a")
           }
       }
   }
   ```

## Step 2: Add Required Permissions

Add these permissions to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Step 3: Add ProGuard Rules

Add these rules to `proguard-rules.pro`:
```proguard
-keep class com.**.TNative$aa { public *; }
-keep class com.**.TNative$aa$bb { public *; }
-keep class com.**.TNative$bb { *; }
-keep class com.**.TNative$bb$I { *; }
```

## Step 4: Add WebView to Login Layout

Add WebView to your login layout (`activity_login.xml`):
```xml
<WebView
    android:id="@+id/tcaptchaWebview"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:visibility="gone"
    app:layout_constraintTop_toBottomOf="@id/registerButton"
    app:layout_constraintBottom_toTopOf="@id/errorText" />
```

## Step 5: Implement CAPTCHA in LoginActivity

1. Setup WebView:
```kotlin
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
}
```

2. Initialize CAPTCHA SDK using the provided interfaces:
```kotlin
private fun setupCaptcha() {
    try {
        // Define the privacy policy implementation
        val privacyPolicy = object : TencentCaptchaConfig.ITencentCaptchaPrivacyPolicy {
            override fun userAgreement(): Boolean {
                // IMPORTANT: In a production environment, you should implement proper privacy policy checks
                // Return values:
                // - true: User has agreed to the privacy policy, SDK will work normally
                // - false: User has not agreed to the privacy policy, SDK integration will be blocked
                // Note: Returning true here is only for demonstration purposes
                return true
            }
        }

        // Define the device info provider implementation
        val deviceInfoProvider = object : TencentCaptchaConfig.ICaptchaDeviceInfoProvider {
            override fun getAndroidId(): String {
                return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            }
        }

        // Initialize the CAPTCHA SDK
        val configBuilder = TencentCaptchaConfig.Builder(applicationContext, privacyPolicy, deviceInfoProvider)
        val ret = TencentCaptcha.init(configBuilder.build())
        if (ret != RetCode.OK) {
            Log.e(TAG, "Initialization failed: ${ret.code}, ${ret.msg}")
            return
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error setting up CAPTCHA: ${e.message}", e)
    }
}
```

3. Implement CAPTCHA verification:
```kotlin
private fun startCaptchaVerification(email: String, password: String) {
    try {
        val captchaWebView = binding.root.findViewById<WebView>(R.id.tcaptchaWebview)
        captchaWebView.visibility = View.VISIBLE

        val captchaParamBuilder = TencentCaptchaParam.Builder()
            .setWebView(captchaWebView)
            .setCaptchaAppid("YOUR_CAPTCHA_APP_ID")

        val captchaCallback = object : TencentCaptchaCallback {
            override fun finish(ret: RetCode, resultObject: JSONObject?) {
                runOnUiThread {
                    captchaWebView.visibility = View.GONE
                    if (ret == RetCode.OK) {
                        viewModel.login(email, password)
                    } else {
                        showError("CAPTCHA verification failed")
                    }
                }
            }

            override fun exception(t: Throwable) {
                runOnUiThread {
                    captchaWebView.visibility = View.GONE
                    showError("CAPTCHA verification error")
                }
            }
        }

        TencentCaptcha.start(captchaCallback, captchaParamBuilder.build())
    } catch (e: Exception) {
        Log.e(TAG, "Error starting CAPTCHA", e)
    }
}
```

## Important Notes

1. **Interface Usage**: The SDK provides the necessary interfaces:
   - `TencentCaptchaConfig.ITencentCaptchaPrivacyPolicy`
     - The `userAgreement()` function must return `true` for the SDK to work
     - In production, implement proper privacy policy checks before returning `true`
     - Returning `false` will prevent SDK integration
   - `TencentCaptchaConfig.ICaptchaDeviceInfoProvider`

2. **Privacy Policy Implementation**:
   - The demo returns `true` for simplicity
   - In a real application, you should:
     - Show your privacy policy to users
     - Get explicit user consent
     - Store user's privacy preferences
     - Return the actual user agreement status

3. **WebView Settings**: Ensure all required WebView settings are enabled for proper CAPTCHA functionality.

4. **UI Thread**: Always update UI elements on the main thread using `runOnUiThread`.

5. **Error Handling**: Implement proper error handling and user feedback.

6. **Testing**: Test the CAPTCHA implementation thoroughly with different network conditions.

## Troubleshooting

1. **CAPTCHA Not Showing**:
   - Check WebView settings
   - Verify internet connectivity
   - Check logcat for initialization errors
   - Verify privacy policy agreement is returning `true`

2. **Initialization Failures**:
   - Verify App ID is correct
   - Check internet permissions
   - Verify AAR file is properly included
   - Check privacy policy implementation

3. **Common Issues**:
   - WebView JavaScript disabled
   - Missing internet permission
   - Incorrect thread handling
   - Missing ProGuard rules
   - Privacy policy returning `false`

## References

- [Tencent Cloud CAPTCHA Android Client SDK Integration Documentation](https://www.tencentcloud.com/document/product/1159/67265)
- [Tencent Cloud CAPTCHA SDK Download](https://www.tencentcloud.com/document/product/1159/67267)
- [Android WebView Documentation](https://developer.android.com/reference/android/webkit/WebView) 