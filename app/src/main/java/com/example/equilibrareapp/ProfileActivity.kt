package com.example.equilibrareapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.equilibrareapp.auth.ui.LoginActivity
import com.example.equilibrareapp.database.user.UserDatabase
import com.example.equilibrareapp.databinding.ActivityProfileBinding
import com.example.equilibrareapp.model.User
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.repository.UserRepository
import com.example.equilibrareapp.service.ApiConfig.Companion.getApiService
import com.example.equilibrareapp.service.LoginEmailResponse
import com.example.equilibrareapp.service.LogoutRequest
import com.example.equilibrareapp.service.LogoutResponse
import com.example.equilibrareapp.utils.uriToFile
import com.example.equilibrareapp.viewmodel.UserViewModel
import com.example.equilibrareapp.viewmodel.UserViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var userViewModel: UserViewModel
    private var getFile: File? = null
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


    private fun setupClickListener() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.btnPhotoProfile.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val fileUri = data?.data

                fileUri?.let {
                    Glide.with(this)
                        .load(it)
                        .transform(CenterCrop(), RoundedCorners(16))
                        .into(binding.ciProfile)
                    getFile = uriToFile(it, this)
                }
            }
        }.launch(chooser)
    }


    private fun fetchUserProfile() {
        val userDao = UserDatabase.getDatabase(application).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)

        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        preferenceHelper.getUserUid()?.let {
            userViewModel.getUserByUid(it) { user ->
                user?.let {
                    displayUserData(user)
                }
            }
        }
    }

    private fun displayUserData(user: User) {
        binding.tvUsername.text = user.displayName
        binding.tvEmail.text = user.email
        Glide.with(this)
            .load(user.photoURL)
            .into(binding.ciProfile)
    }

    private fun logout() {
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
                        preferenceHelper.getUserUid()?.let { userViewModel.deleteUserByUid(it) }
                        preferenceHelper.clearPref()
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