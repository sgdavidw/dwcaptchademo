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
    android:layout_height="200dp"
    android:layout_marginTop="8dp"
    android:visibility="gone"
    app:layout_constraintTop_toBottomOf="@id/loginButton" />
```

## Step 5: Implement CAPTCHA in LoginActivity

1. Setup WebView with required settings:
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
        javaScriptCanOpenWindowsAutomatically = true
        setSupportMultipleWindows(true)
        cacheMode = WebSettings.LOAD_NO_CACHE
        mediaPlaybackRequiresUserGesture = false
        useWideViewPort = true
        loadWithOverviewMode = true
    }
    WebView.setWebContentsDebuggingEnabled(true)  // Enable for debugging
}
```

2. Initialize CAPTCHA SDK:
```kotlin
private fun setupCaptcha() {
    val privacyPolicy = object : TencentCaptchaConfig.ITencentCaptchaPrivacyPolicy {
        override fun userAgreement(): Boolean {
            // IMPORTANT: In production, implement proper privacy policy checks
            // Return true if user agreed to privacy policy, false otherwise
            return true  // Demo implementation
        }
    }

    val deviceInfoProvider = object : TencentCaptchaConfig.ICaptchaDeviceInfoProvider {
        override fun getAndroidId(): String {
            return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }
    }

    val configBuilder = TencentCaptchaConfig.Builder(applicationContext, privacyPolicy, deviceInfoProvider)
    TencentCaptcha.init(configBuilder.build())
}
```

3. Implement CAPTCHA verification:
```kotlin
private fun startCaptchaVerification(email: String, password: String) {
    val captchaWebView = binding.root.findViewById<WebView>(R.id.tcaptchaWebview)
    captchaWebView.visibility = View.VISIBLE

    val captchaParamBuilder = TencentCaptchaParam.Builder()
        .setWebView(captchaWebView)
        .setCaptchaAppid("YOUR_CAPTCHA_APP_ID")
        .setType("popup")
        .setEnableDarkMode(false)
        .setLoading(true)
        .setUserLanguage("en")
        .setNeedFeedBack(true)
        .setEnableAged(true)

    val captchaCallback = object : TencentCaptchaCallback {
        override fun finish(ret: RetCode, resultObject: JSONObject?) {
            if (ret == RetCode.OK && resultObject?.has("ticket") == true) {
                val ticket = resultObject.getString("ticket")
                viewModel.login(email, password, ticket)
            }
        }
    }

    TencentCaptcha.start(captchaCallback, captchaParamBuilder.build())
}
```

## Important Notes

1. **Privacy Policy Implementation**:
   - The `userAgreement()` function in `ITencentCaptchaPrivacyPolicy`:
     - Return `true` if the user has agreed to the privacy policy
     - Return `false` if the user has not agreed
     - SDK will only work when `true` is returned
   - In production:
     - Show your privacy policy to users
     - Get explicit user consent
     - Store user's privacy preferences
     - Return the actual agreement status

2. **WebView Settings**:
   - Enable JavaScript and DOM storage
   - Allow file and content access
   - Enable database and geolocation
   - Configure for popup display

3. **Error Handling**:
   - Implement proper error handling
   - Log CAPTCHA events for debugging
   - Show user-friendly error messages

4. **UI Thread**:
   - Update UI elements on the main thread
   - Use `runOnUiThread` for WebView visibility changes

5. **Testing**:
   - Test with different network conditions
   - Verify CAPTCHA behavior in various scenarios
   - Check error handling and user feedback

## Troubleshooting

1. **CAPTCHA Not Showing**:
   - Verify WebView settings
   - Check internet connectivity
   - Confirm privacy policy returns `true`
   - Check logcat for initialization errors

2. **Initialization Failures**:
   - Verify App ID is correct
   - Check internet permissions
   - Verify AAR file is properly included

3. **Common Issues**:
   - WebView JavaScript disabled
   - Missing internet permission
   - Incorrect thread handling
   - Missing ProGuard rules

## References

- [Tencent Cloud CAPTCHA Documentation](https://www.tencentcloud.com/document/product/1159/67265?lang=en&pg=)
- [Android WebView Documentation](https://developer.android.com/reference/android/webkit/WebView) 