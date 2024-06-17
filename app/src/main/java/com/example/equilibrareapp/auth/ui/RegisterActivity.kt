package com.example.equilibrareapp.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.equilibrareapp.databinding.ActivityRegisterBinding
import com.example.equilibrareapp.preference.PreferenceHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceHelper = PreferenceHelper(this)
        setupOnClickListener()

    }

    private fun setupOnClickListener() {
        binding.btnRegister.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
//            val profileUrl =binding.usernameEditText.text.toString()
            when {
                username.isEmpty() -> {
                    binding.usernameEditText.error = "Username perlu diisi"
                }

                email.isEmpty() -> {
                    binding.emailEditText.error = "Email perlu diisi"
                }

                !email.contains("@") -> {
                    binding.emailEditText.error = "Email tidak valid"
                }

                password.isEmpty() -> {
                    binding.passwordEditText.error = "Password perlu diisi"
                }

                else -> {
                    showLoading(true)
                    register()
                }
            }
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun register() {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finish()
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
}