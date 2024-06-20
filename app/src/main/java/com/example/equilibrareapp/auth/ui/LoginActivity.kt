package com.example.equilibrareapp.auth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.equilibrareapp.LandingActivity
import com.example.equilibrareapp.database.user.UserDatabase
import com.example.equilibrareapp.databinding.ActivityLoginBinding
import com.example.equilibrareapp.model.User
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.repository.UserRepository
import com.example.equilibrareapp.service.ApiConfig.Companion.getApiService
import com.example.equilibrareapp.service.LoginEmailResponse
import com.example.equilibrareapp.service.LoginRequest
import com.example.equilibrareapp.service.ProfileResponse
import com.example.equilibrareapp.viewmodel.UserViewModel
import com.example.equilibrareapp.viewmodel.UserViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        preferenceHelper = PreferenceHelper(this)

        // Inisialisasi ViewModel
        val userDao = UserDatabase.getDatabase(application).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

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
                        val token = responseBody.loginResult.idToken
                        preferenceHelper.saveUserToken(token)
                        preferenceHelper.setStatusLogin(true)
                        fetchUserProfile(token)
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal: ${responseBody?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginEmailResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, "Login Gagal: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "onFailure: ${t.message}")
            }
        })
    }

    private fun fetchUserProfile(token: String) {
        val client = getApiService().getProfile("Bearer $token")
        client.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    if (userProfile != null) {
                        val user = User(
                            uid = userProfile.user.uid,
                            email = userProfile.user.email,
                            displayName = userProfile.user.displayName,
                            photoURL = userProfile.user.photoURL
                        )
                        userViewModel.insertUser(user)
                        Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                        val main = Intent(this@LoginActivity, LandingActivity::class.java)
                        startActivity(main)
                        finishAffinity()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Gagal mengambil profil user: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Gagal mengambil profil user: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
