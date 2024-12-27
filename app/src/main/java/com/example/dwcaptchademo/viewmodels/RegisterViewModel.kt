package com.example.dwcaptchademo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dwcaptchademo.api.RegisterRequest
import com.example.dwcaptchademo.api.RetrofitClient
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.utils.ValidationUtils
import com.example.dwcaptchademo.utils.safeApiCall
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val _registerResult = MutableLiveData<Resource<Unit>>()
    val registerResult: LiveData<Resource<Unit>> = _registerResult

    fun register(name: String, email: String, password: String) {
        val validationError = ValidationUtils.validateRegisterInput(name, email, password)
        if (validationError != null) {
            _registerResult.value = Resource.Error(validationError)
            return
        }

        viewModelScope.launch {
            _registerResult.value = Resource.Loading
            val response = safeApiCall {
                RetrofitClient.authApi.register(RegisterRequest(email, password, name))
            }

            when (response) {
                is Resource.Success -> {
                    _registerResult.value = Resource.Success(Unit)
                }
                is Resource.Error -> {
                    _registerResult.value = Resource.Error(response.message)
                }
                Resource.Loading -> {} // No-op
            }
        }
    }
} 