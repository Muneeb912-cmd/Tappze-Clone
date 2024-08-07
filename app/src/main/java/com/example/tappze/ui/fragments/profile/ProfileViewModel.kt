package com.example.tappze.com.example.tappze.ui.fragments.profile

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tappze.com.example.tappze.repository.SocialLinksRepository
import com.example.tappze.com.example.tappze.utils.AppIntentUtil
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val manageLinks: SocialLinksRepository,
) : ViewModel() {

    private val _saveLinksState = MutableStateFlow<Response<Unit>>(Response.Loading)
    val saveLinksState: StateFlow<Response<Unit>> = _saveLinksState

    private val _linksState = MutableStateFlow<Map<String, String>>(emptyMap())
    val linksState: StateFlow<Map<String, String>> = _linksState

    var links = MutableLiveData<Map<String, String>>().apply {
        value = emptyMap()
    }

    fun getSavedLinks(userId: String) {
        viewModelScope.launch {
            _saveLinksState.value = Response.Loading
            try {
                when (val existingLinksResponse = manageLinks.getExistingLinks(userId)) {
                    is Response.Success -> {
                        val existingLinks = existingLinksResponse.data
                        val updatedLinks = existingLinks.links.toMutableMap()
                        withContext(Dispatchers.Main) {
                            links.value = updatedLinks
                        }
                        _saveLinksState.value = Response.Success(Unit)
                    }

                    is Response.Error -> {
                        // Handle the error response
                        withContext(Dispatchers.Main) {
                            links.value = emptyMap()
                        }
                        _saveLinksState.value = existingLinksResponse
                    }

                    Response.Loading -> {
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _saveLinksState.value = Response.Error(e)
                    links.value = emptyMap()
                }
            }
        }
    }

    fun observeLinks(userId: String) {
        manageLinks.observeLinks(userId) { links ->
            _linksState.value = links?.links?.toMap() ?: emptyMap()
        }
    }
}
