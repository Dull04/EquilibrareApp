package com.example.equilibrareapp.auth.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

class RegisterViewModel : ViewModel() {

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?>
        get() = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?>
        get() = _passwordError

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean>
        get() = _registerSuccess

    private val _navigateToLogin = MutableLiveData<Unit>()
    val navigateToLogin: LiveData<Unit>
        get() = _navigateToLogin

    fun onRegisterClicked() {
        val usernameValue = username.value ?: ""
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""

        if (validateInput(usernameValue, emailValue, passwordValue)) {
            // Lakukan proses register, misalnya simpan data ke server
            _registerSuccess.value = true
        } else {
            _registerSuccess.value = false
        }
    }

    fun onLoginClicked() {
        _navigateToLogin.value = Unit
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            isValid = false
        }

        if (email.isEmpty()) {
            _emailError.value = "Email tidak boleh kosong"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Email tidak valid"
            isValid = false
        } else {
            _emailError.value = null
        }

        if (password.isEmpty()) {
            _passwordError.value = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 8) {
            _passwordError.value = "Password harus lebih dari 8 karakter"
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }
}