package com.example.equilibrareapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.equilibrareapp.auth.ui.LoginActivity
import com.example.equilibrareapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        Thread.sleep(1000)
        installSplashScreen()
        setContentView(binding.root)
        preferenceHelper = PreferenceHelper(this)
        if (preferenceHelper.getStatusLogin()) {
            navigateToMain()
            return
        }
        navigateToLogin()
    }

    private fun navigateToMain() {
        val main = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(main)
        finish()
    }

    private fun navigateToLogin() {
        val main = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(main)
        finish()
    }
}