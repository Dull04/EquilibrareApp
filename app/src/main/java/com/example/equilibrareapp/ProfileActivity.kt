package com.example.equilibrareapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.equilibrareapp.auth.ui.LoginActivity
import com.example.equilibrareapp.databinding.ActivityProfileBinding
import com.example.equilibrareapp.model.User
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.service.ApiConfig.Companion.getApiService
import com.example.equilibrareapp.service.LoginEmailResponse
import com.example.equilibrareapp.service.LogoutRequest
import com.example.equilibrareapp.service.LogoutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        preferenceHelper = PreferenceHelper(this)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        fetchUserProfile()
        setupClickListener()
    }

    private fun fetchUserProfile() {
        val user = preferenceHelper.getUser()
        if (user != null) {
            displayUserData(user)
        } else {
            Toast.makeText(
                this@ProfileActivity,
                "Profil pengguna belum didapatkan",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayUserData(user: User) {
        binding.tvUsername.text = user.displayName
        binding.tvEmail.text = user.email
        Glide.with(this)
            .load(user.photoURL)
            .into(binding.ciProfile)
    }

    private fun setupClickListener() {
        binding.btnLogout.setOnClickListener {
            val uid = preferenceHelper.getUserUid().toString()
            val logoutRequest = LogoutRequest(uid)
            val client = getApiService().logout(logoutRequest)
            client.enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(
                    call: Call<LogoutResponse>,
                    response: Response<LogoutResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            preferenceHelper.clearPref()
                            preferenceHelper.clearUserData()
                            Toast.makeText(
                                this@ProfileActivity,
                                "Berhasil Logout",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val main = Intent(this@ProfileActivity, LoginActivity::class.java)
                            startActivity(main)
                            finishAffinity()
                        } else {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Logout Gagal: ${responseBody?.message ?: "Unknown error"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Logout Gagal: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Logout Gagal onFailure: ${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e("TAG", "onFailure: ${t.message}")
                }
            })
        }
    }
}