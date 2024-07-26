package com.example.tappze.repository

import android.graphics.Bitmap
import com.example.tappze.com.example.tappze.models.Links
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.utils.Response

interface UserRepository {

    suspend fun signUpWithEmailPassword(
        email: String,
        password: String,
        name: String,
        userName: String
    ): Response<Unit>

    suspend fun signInWithEmailPassword(email: String, password: String): Response<User>
    suspend fun getUserData(userId: String): Response<User>
    suspend fun getExistingLinks(userId: String): Response<Links>
    suspend fun addLink(userId: String, appName: String, link: String): Response<Unit>
    suspend fun uploadImageAndUpdateUser(userData: User): Response<Unit>
    fun generateQRCode(url: String): Bitmap?
    suspend fun sendChangePasswordEmail(email:String):Response<Unit>
}
