package com.example.dwcaptchademo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dwcaptchademo.api.RetrofitClient
import com.example.dwcaptchademo.api.UserProfile
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.utils.TokenManager
import com.example.dwcaptchademo.utils.safeApiCall
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager.getInstance(application)
    private val _profileData = MutableLiveData<Resource<UserProfile>>()
    val profileData: LiveData<Resource<UserProfile>> = _profileData

    fun fetchProfile() {
        val token = tokenManager.getToken()
        if (token == null) {
            _profileData.value = Resource.Error("Not logged in")
            return
        }

        viewModelScope.launch {
            _profileData.value = Resource.Loading
            val response = safeApiCall {
                RetrofitClient.authApi.getProfile("Bearer $token")
            }

            when (response) {
                is Resource.Success -> {
                    _profileData.value = Resource.Success(response.data)
                }
                is Resource.Error -> {
                    if (response.message.contains("401")) {
                        tokenManager.deleteToken()
                    }
                    _profileData.value = Resource.Error(response.message)
                }
                Resource.Loading -> {} // No-op
            }
        }
    }

    fun logout() {
        tokenManager.deleteToken()
    }
} 