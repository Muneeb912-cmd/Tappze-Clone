package com.example.tappze.com.example.tappze.repository

import android.graphics.Bitmap
import com.example.tappze.com.example.tappze.models.Links
import com.example.tappze.utils.Response

interface SocialLinksRepository {
    suspend fun getExistingLinks(userId: String): Response<Links>
    suspend fun addLink(userId: String, appName: String, link: String): Response<Unit>

    fun generateQRCode(url: String): Bitmap?
    fun stopObservingLinks()
    fun observeLinks(userId: String, onLinksUpdated: (Links?) -> Unit)
    suspend fun deleteLink(linkToDelete: String):Response<Unit>
}