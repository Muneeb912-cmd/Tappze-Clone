package com.example.tappze.com.example.tappze.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.tappze.com.example.tappze.models.User
import com.google.gson.Gson


class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    fun saveUser(user: User){
        val gson = Gson()
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString("user", userJson).apply()
    }


    fun getUser(): User? {
        val userJson = sharedPreferences.getString("user", null)
        val gson = Gson()
        val user = if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
        return user
    }

    fun updateUser(updater: (User) -> User) {
        val currentUser = getUser()
        if (currentUser != null) {
            val updatedUser = updater(currentUser)
            saveUser(updatedUser)
        }
    }
    fun deleteUser() {
        sharedPreferences.edit().remove("user").apply()
    }
}