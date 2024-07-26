package com.example.tappze.com.example.tappze.models

import java.io.Serializable

data class User(
    val fullName: String? = null,
    val userName: String? = null,
    var photo: String? = null,
    val email: String? = null,
    val userId: String? = null,
    val gender: String? = null,
    val dob: String? = null,
    val company: String? = null,
    val aboutYourself: String? = null,
    var phone: String? = null,
):Serializable
