package com.example.equilibrareapp.auth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.equilibrareapp.LandingActivty
import com.example.equilibrareapp.databinding.ActivityLoginBinding
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.service.ApiConfig.Companion.getApiService
import com.example.equilibrareapp.service.LoginEmailResponse
import com.example.equilibrareapp.service.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                    login(email, password)
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

    private fun login(email: String, password: String) {

        val loginRequest = LoginRequest(email, password)
        val client = getApiService().loginEmail(loginRequest)
        client.enqueue(object : Callback<LoginEmailResponse> {
            override fun onResponse(
                call: Call<LoginEmailResponse>,
                response: Response<LoginEmailResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        preferenceHelper.saveUserToken(responseBody.loginResult.idToken)
                        preferenceHelper.saveUserUid(responseBody.loginResult.uid)
                        preferenceHelper.setStatusLogin(true)
                        Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT)
                            .show()
                        val main = Intent(this@LoginActivity, LandingActivty::class.java)
                        startActivity(main)
                        finishAffinity()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Gagal: ${responseBody?.message ?: "Unknown error"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login Gagal: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginEmailResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, "Login Gagal: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
