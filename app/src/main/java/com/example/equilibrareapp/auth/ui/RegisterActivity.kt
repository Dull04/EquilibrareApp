package com.example.equilibrareapp.auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.equilibrareapp.R
import com.example.equilibrareapp.auth.model.RegisterViewModel
import com.example.equilibrareapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.registerViewModel = registerViewModel

        registerViewModel.registerSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        registerViewModel.navigateToLogin.observe(this, Observer {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}