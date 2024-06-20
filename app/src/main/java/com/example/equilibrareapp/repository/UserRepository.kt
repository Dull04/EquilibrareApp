package com.example.equilibrareapp.repository

import com.example.equilibrareapp.database.user.UserDao
import com.example.equilibrareapp.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserByUid(uid: String): User? {
        return userDao.getUserByUid(uid)
    }

    suspend fun deleteUserByUid(uid: String) {
        userDao.deleteUserByUid(uid)
    }

    suspend fun updateProfile(uid: String, displayName: String, email: String, photoURL: String) {
        userDao.updateUser(uid, displayName, email, photoURL)
    }
}