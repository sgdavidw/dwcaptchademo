package com.example.dwcaptchademo.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dwcaptchademo.databinding.ActivityRegisterBinding
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.viewmodels.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.register(name, email, password)
        }

        binding.backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
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
        binding.registerButton.isEnabled = !isLoading
        binding.backToLoginButton.isEnabled = !isLoading
        binding.nameInput.isEnabled = !isLoading
        binding.emailInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        binding.errorText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }
} 