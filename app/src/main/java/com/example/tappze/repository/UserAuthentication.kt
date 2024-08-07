package com.example.tappze.com.example.tappze.repository

import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.utils.Response

interface UserAuthentication {
    suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        name: String,
        userName: String
    ): Response<Unit>

    suspend fun signInWithEmailPassword(email: String, password: String): Response<User>
    suspend fun sendChangePasswordEmail(email:String):Response<Unit>
}