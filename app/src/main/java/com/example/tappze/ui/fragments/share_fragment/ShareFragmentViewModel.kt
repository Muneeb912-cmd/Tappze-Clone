package com.example.tappze.com.example.tappze.ui.fragments.share_fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.tappze.BuildConfig
import com.example.tappze.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class ShareFragmentViewModel @Inject constructor(
    private val userRepository: UserRepository
):ViewModel() {
    fun generateQR(url:String): Bitmap? {
        return userRepository.generateQRCode(url)
    }

    fun shareUserIntent(context: Context,link:String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Hey check out my Tappze ID!\n$link"
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

}