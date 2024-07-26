package com.example.tappze.com.example.tappze.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SocialLinks(val imageResId: Int, val text: String):Parcelable
