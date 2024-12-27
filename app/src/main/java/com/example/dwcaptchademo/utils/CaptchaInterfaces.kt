package com.example.dwcaptchademo.utils

import com.tencent.captcha.sdk.TencentCaptchaConfig

/**
 * Interface for privacy policy agreement
 */
interface ITencentCaptchaPrivacyPolicy : TencentCaptchaConfig.ITencentCaptchaPrivacyPolicy {
    /**
     * Check if user has agreed to privacy policy
     * @return Boolean true if user has agreed
     */
    override fun userAgreement(): Boolean
}

/**
 * Interface for device information provider
 */
interface ICaptchaDeviceInfoProvider : TencentCaptchaConfig.ICaptchaDeviceInfoProvider {
    /**
     * Get Android device ID
     * @return String device ID
     */
    override fun getAndroidId(): String
} 