package com.example.equilibrareapp.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.equilibrareapp.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserByUid(uid: String): User?

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserByUid(uid: String)

    @Query("UPDATE users SET displayName= :displayName, email= :email, photoURL= :photoURL WHERE uid= :uid")
    suspend fun updateUser(uid: String, displayName: String, email: String, photoURL: String)
}