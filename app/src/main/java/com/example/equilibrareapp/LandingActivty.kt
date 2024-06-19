package com.example.equilibrareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.equilibrareapp.databinding.ActivityLandingBinding
import com.example.equilibrareapp.model.User
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.service.ApiConfig.Companion.getApiService
import com.example.equilibrareapp.service.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LandingActivty : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        preferenceHelper = PreferenceHelper(this)
        setContentView(binding.root)
        supportActionBar?.hide()
        fetchUser()
        setupOnclickListener()
    }

    private fun fetchUser() {
        val user = preferenceHelper.getUser()
        if (user != null) {
            displayUserData(user)
        } else {
            val token = "Bearer ${preferenceHelper.getUserToken()}"
            val client = getApiService().getProfile(token)
            client.enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { profileResponse ->
                            val user = User(
                                profileResponse.user.uid,
                                profileResponse.user.email,
                                profileResponse.user.displayName,
                                profileResponse.user.photoURL
                            )
                            preferenceHelper.saveUser(user)
                            displayUserData(user)
                        }
                    } else {
                        Toast.makeText(
                            this@LandingActivty,
                            "Gagal mendapatkan profil: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LandingActivty,
                        "Gagal mendapatkan profil: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun displayUserData(user: User) {
        binding.tvWelcome.text = "Hi, Welcome Back ${user.displayName}!"
        Glide.with(this)
            .load(user.photoURL)
            .into(binding.btnProfile)
    }

    private fun setupOnclickListener() {
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this@LandingActivty, ProfileActivity::class.java))

        }
        binding.btnWrite.setOnClickListener {
            startActivity(Intent(this@LandingActivty, MainActivity::class.java))
        }
    }
}