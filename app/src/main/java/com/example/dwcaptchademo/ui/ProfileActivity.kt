package com.example.dwcaptchademo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dwcaptchademo.databinding.ActivityProfileBinding
import com.example.dwcaptchademo.utils.Resource
import com.example.dwcaptchademo.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
        viewModel.fetchProfile()
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.profileData.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    binding.nameText.text = result.data.name
                    binding.emailText.text = result.data.email
                    binding.errorText.visibility = View.GONE
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message)
                    if (result.message.contains("Not logged in")) {
                        startActivity(Intent(this, LoginActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        finish()
                    }
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.logoutButton.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        binding.errorText.apply {
            text = message
            visibility = View.VISIBLE
        }
    }
} 