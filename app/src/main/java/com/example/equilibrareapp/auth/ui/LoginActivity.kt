package com.example.equilibrareapp.auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.equilibrareapp.MainActivity
import com.example.equilibrareapp.auth.model.LoginViewModel
import com.example.equilibrareapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this

        loginViewModel.loginSuccess.observe(this) { success ->
            if (success) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Login gagal. Periksa email dan password Anda.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        loginViewModel.navigateToRegister.observe(this) {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
