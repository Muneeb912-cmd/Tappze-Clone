package com.example.tappze.com.example.tappze.utils

import android.view.View
import android.widget.Button
import android.widget.ProgressBar

class ButtonStateUtils(private val buttonText:String){
    fun showLoadingIndicator(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        createAccountBtn.isEnabled = false
        createAccountBtn.text = ""
        loadingIndicator.visibility = View.VISIBLE
    }

    fun hideLoadingIndicator(createAccountBtn: Button, loadingIndicator: ProgressBar) {
        createAccountBtn.isEnabled = true
        createAccountBtn.text=buttonText
        loadingIndicator.visibility = View.GONE
    }
}