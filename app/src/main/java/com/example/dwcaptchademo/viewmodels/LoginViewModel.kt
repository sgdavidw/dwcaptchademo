package com.example.dwcaptchademo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dwcaptchademo.api.LoginRequest
import com.example.dwcaptchademo.api.RetrofitClient
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.utils.TokenManager
import com.example.dwcaptchademo.utils.ValidationUtils
import com.example.dwcaptchademo.utils.safeApiCall
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager.getInstance(application)
    private val _loginResult = MutableLiveData<Resource<Unit>>()
    val loginResult: LiveData<Resource<Unit>> = _loginResult

    fun login(email: String, password: String) {
        val validationError = ValidationUtils.validateLoginInput(email, password)
        if (validationError != null) {
            _loginResult.value = Resource.Error(validationError)
            return
        }

        viewModelScope.launch {
            _loginResult.value = Resource.Loading
            val response = safeApiCall {
                RetrofitClient.authApi.login(LoginRequest(email, password))
            }

            when (response) {
                is Resource.Success -> {
                    tokenManager.saveToken(response.data.access_token)
                    _loginResult.value = Resource.Success(Unit)
                }
                is Resource.Error -> {
                    _loginResult.value = Resource.Error(response.message)
                }
                Resource.Loading -> {} // No-op
            }
        }
    }

    fun isLoggedIn(): Boolean = tokenManager.hasToken()
} 