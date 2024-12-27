package com.example.dwcaptchademo.utils

import retrofit2.Response
import java.net.UnknownHostException

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                Resource.Success(it)
            } ?: Resource.Error("Response body is empty")
        } else {
            Resource.Error("Error: ${response.code()} - ${response.message()}")
        }
    } catch (e: UnknownHostException) {
        Resource.Error("No internet connection")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "An unknown error occurred")
    }
}

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 && 
               password.any { it.isDigit() } && 
               password.any { it.isLetter() }
    }

    fun validateLoginInput(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !isValidEmail(email) -> "Invalid email format"
            password.isBlank() -> "Password cannot be empty"
            !isValidPassword(password) -> "Password must be at least 8 characters with at least one letter and one number"
            else -> null
        }
    }

    fun validateRegisterInput(name: String, email: String, password: String): String? {
        return when {
            name.isBlank() -> "Name cannot be empty"
            email.isBlank() -> "Email cannot be empty"
            !isValidEmail(email) -> "Invalid email format"
            password.isBlank() -> "Password cannot be empty"
            !isValidPassword(password) -> "Password must be at least 8 characters with at least one letter and one number"
            else -> null
        }
    }
} 