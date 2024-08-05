package com.example.tappze.com.example.tappze.ui.fragments.add_edit_link

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tappze.adapters.SocialLinkAdapter
import com.example.tappze.com.example.tappze.utils.AppIntentUtil
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditLinkViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appIntentUtil: AppIntentUtil
) : ViewModel() {

    private val _saveLinksState = MutableStateFlow<Response<Unit>>(Response.Loading)
    val saveLinksState: StateFlow<Response<Unit>> = _saveLinksState
    private val _navigateToProfile = MutableLiveData<Intent?>()
    val navigateToProfile: MutableLiveData<Intent?> = _navigateToProfile
    private val _showAlertDialog = MutableLiveData<Boolean>()
    val showAlertDialog: LiveData<Boolean> = _showAlertDialog

    fun addLink(userId: String, appName: String, link: String) {
        viewModelScope.launch {
            _saveLinksState.value = Response.Loading
            _saveLinksState.value = userRepository.addLink(userId, appName, link)
        }
    }

    fun deleteLink(linkToDelete: String) {
        viewModelScope.launch {
            _saveLinksState.value = Response.Loading
            try {
                when (val response = userRepository.deleteLink(linkToDelete)) {
                    is Response.Success -> {
                        _saveLinksState.value = Response.Success(Unit)
                    }

                    is Response.Error -> {
                        _saveLinksState.value = response
                    }

                    Response.Loading -> {
                        // Handle if needed
                    }
                }
            } catch (e: Exception) {
                _saveLinksState.value = Response.Error(e)
            }
        }
    }

    fun openProfile(appName: String, id: String, context: Context) {
        val intent: Intent? = when (appName) {
            "Instagram" -> appIntentUtil.openInstagramProfile(id)
            "Facebook" -> appIntentUtil.openFacebookProfile(id)
            "TikTok" -> appIntentUtil.openTikTokProfile(id)
            "WhatsApp" -> appIntentUtil.openWhatsappChat(id)
            "LinkedIn" -> appIntentUtil.openLinkedInProfile(id)
            "Telegram" -> appIntentUtil.openTelegramChat(id)
            "Snapchat" -> appIntentUtil.openSnapchatProfile(id)
            "X" -> appIntentUtil.openXProfile(id)
            "Website" -> appIntentUtil.openWebsite(id)
            "YouTube" -> appIntentUtil.openYouTubeChannel(id)
            "Spotify" -> appIntentUtil.openSpotifyPlaylist(id)
            "Email" -> appIntentUtil.sendEmail(id)
            "PayPal" -> appIntentUtil.openPayPal(id)
            "Pinterest" -> appIntentUtil.openPinterestProfile(id)
            "Skype" -> appIntentUtil.openSkypeCall(id)
            "Calendly" -> appIntentUtil.openCalendly(id)
            "Phone" -> appIntentUtil.openPhone(id)
            else -> null
        }

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
