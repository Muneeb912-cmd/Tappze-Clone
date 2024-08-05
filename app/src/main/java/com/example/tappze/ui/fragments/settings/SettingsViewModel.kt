package com.example.tappze.com.example.tappze.ui.fragments.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tappze.com.example.tappze.utils.AppIntentUtil
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appIntentUtil: AppIntentUtil
):ViewModel() {

    private val _navigateToProfile = MutableLiveData<Intent?>()
    val navigateToProfile: MutableLiveData<Intent?> = _navigateToProfile
    private val _showAlertDialog = MutableLiveData<Boolean>()
    val showAlertDialog: LiveData<Boolean> = _showAlertDialog
    fun gmailIntent(context: Context) {
        val intent: Intent? = appIntentUtil.sendEmail("emanmuneeb1@gmail.com")

        if (intent != null) {
            if (intent.resolveActivity(context.packageManager) != null) {
                _navigateToProfile.postValue(intent)
            } else {
                _showAlertDialog.postValue(true)
            }
        } else {
            _showAlertDialog.postValue(true)
        }
    }
}