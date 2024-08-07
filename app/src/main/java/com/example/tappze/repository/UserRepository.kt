package com.example.tappze.repository

import android.graphics.Bitmap
import com.example.tappze.com.example.tappze.models.Links
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.utils.Response

interface UserRepository {
    suspend fun getUserData(userId: String): Response<User>
    suspend fun uploadImageAndUpdateUser(userData: User): Response<Unit>

}
