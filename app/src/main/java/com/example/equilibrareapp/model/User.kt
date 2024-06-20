package com.example.equilibrareapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val photoURL: String?
)