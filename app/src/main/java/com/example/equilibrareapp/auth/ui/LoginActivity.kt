package com.example.equilibrareapp.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.equilibrareapp.MainActivity
import com.example.equilibrareapp.databinding.ActivityLoginBinding
import com.example.equilibrareapp.preference.PreferenceHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        preferenceHelper = PreferenceHelper(this)
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = "Email kosong"
                }

                !email.contains("@") -> {
                    binding.emailEditText.error = "Email tidak valid"
                }

                password.isEmpty() -> {
                    binding.passwordEditText.error = "Password kosong"
                }

                else -> {
                    showLoading(true)
                    login()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
        }
    }

    private fun login() {
        preferenceHelper.setStatusLogin(true)
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}
