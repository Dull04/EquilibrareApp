package com.example.equilibrareapp.auth.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.equilibrareapp.databinding.ActivityRegisterBinding
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.service.ApiConfig.Companion.getApiService
import com.example.equilibrareapp.service.RegisterResponse
import com.example.equilibrareapp.utils.reduceFileImage
import com.example.equilibrareapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceHelper = PreferenceHelper(this)
        setupOnClickListener()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

    }

    private fun setupOnClickListener() {
        binding.btnRegister.setOnClickListener {
            val displayName = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                displayName.isEmpty() -> {
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

                    val emailReq = email.toRequestBody("text/plain".toMediaTypeOrNull())
                    val passwordReq = password.toRequestBody("text/plain".toMediaTypeOrNull())
                    val displayNameReq =
                        displayName.toRequestBody("text/plain".toMediaTypeOrNull())
                    uploadImage(emailReq, passwordReq, displayNameReq)
                }
            }
        }
        binding.btnLogin.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        binding.profileImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Pilih Gambar")
            startForProfileImageResult.launch(chooser)
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val fileUri = data?.data

                fileUri?.let {
                    Glide.with(this)
                        .load(it)
                        .transform(CenterCrop(), RoundedCorners(16)) // Transformasi gambar
                        .into(binding.profileImage)
                    getFile = uriToFile(it, this)
                }
            }
        }

    private fun register(
        email: RequestBody,
        password: RequestBody,
        displayName: RequestBody,
        photo: MultipartBody.Part
    ) {

        val client = getApiService().register(email, password, displayName, photo)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Register Berhasil",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                this@RegisterActivity,
                                LoginActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Register Gagal: Unkown Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Register Gagal: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@RegisterActivity,
                    "Register Gagal: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun uploadImage(email: RequestBody, password: RequestBody, displayName: RequestBody) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            register(email, password, displayName, imageMultipart)
        } else {
            Toast.makeText(
                this@RegisterActivity,
                "Silakan masukkan gambar profile terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
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

    companion object {
        private const val TAG = "RegisterActivity"
    }
}