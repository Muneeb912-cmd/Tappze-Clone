package com.example.tappze.com.example.tappze.ui.share_fragment

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
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
}