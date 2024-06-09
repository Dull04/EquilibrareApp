package com.example.equilibrareapp.auth.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

class LoginViewModel : ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    private val _navigateToRegister = MutableLiveData<Unit>()
    val navigateToRegister: LiveData<Unit>
        get() = _navigateToRegister

    fun onLoginClicked() {
        val emailValue = email.value ?: ""
        val passwordValue = password.value ?: ""

        if (validateInput(emailValue, passwordValue)) {
            // Lakukan proses login, misalnya autentikasi ke server
            _loginSuccess.value = true
        } else {
            _loginSuccess.value = false
        }
    }

    fun onRegisterClicked() {
        _navigateToRegister.value = Unit
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }

        if (password.isEmpty()) {
            return false
        }

        if (password.length < 8) {
            return false
        }

        return true
    }
}