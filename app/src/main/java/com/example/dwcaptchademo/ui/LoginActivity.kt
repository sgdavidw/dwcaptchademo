package com.example.dwcaptchademo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dwcaptchademo.R
import com.example.dwcaptchademo.databinding.ActivityLoginBinding
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.viewmodels.LoginViewModel

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

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.login(email, password)
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
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