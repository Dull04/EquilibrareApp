package com.example.equilibrareapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.equilibrareapp.model.User
import com.example.equilibrareapp.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun getUserByUid(uid: String, callback: (User?) -> Unit) = viewModelScope.launch {
        val user = repository.getUserByUid(uid)
        callback(user)
    }

    fun deleteUserByUid(uid: String) = viewModelScope.launch {
        repository.deleteUserByUid(uid)
    }
    fun updateUser(uid: String, displayName: String, email: String, photoURL: String) = viewModelScope.launch {
        repository.updateProfile(uid, displayName, email, photoURL)
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}