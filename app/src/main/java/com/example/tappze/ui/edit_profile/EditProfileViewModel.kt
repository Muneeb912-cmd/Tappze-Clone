package com.example.tappze.com.example.tappze.ui.edit_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _updateUserState = MutableStateFlow<Response<Unit>>(Response.Loading)
    val updateUserState: StateFlow<Response<Unit>> = _updateUserState
    private val _saveLinksState = MutableStateFlow<Response<Unit>>(Response.Loading)
    val saveLinksState: StateFlow<Response<Unit>> = _saveLinksState
    private val _links = MutableLiveData<Map<String, String>>()
    val links: LiveData<Map<String, String>> get() = _links

    private val _linksState = MutableStateFlow<Map<String, String>>(emptyMap())
    val linksState: StateFlow<Map<String, String>> = _linksState

    fun uploadImgAndUpdateUser(userData: User) {
        viewModelScope.launch {
            _updateUserState.value = Response.Loading
            _updateUserState.value = userRepository.uploadImageAndUpdateUser(userData)
        }
    }

    fun getSavedLinks(userId: String) {
        viewModelScope.launch {
            _saveLinksState.value = Response.Loading
            try {
                when (val existingLinksResponse = userRepository.getExistingLinks(userId)) {
                    is Response.Success -> {
                        val existingLinks = existingLinksResponse.data
                        val updatedLinks = existingLinks.links.toMutableMap()
                        _links.value = updatedLinks
                        _saveLinksState.value = Response.Success(Unit)
                    }

                    is Response.Error -> {
                        _saveLinksState.value = existingLinksResponse
                        _links.value = emptyMap() // or handle as needed
                    }

                    Response.Loading -> {
                        // Handle if needed
                    }
                }
            } catch (e: Exception) {
                _saveLinksState.value = Response.Error(e)
                _links.value = emptyMap() // or handle as needed
            }
        }
    }

    fun observeLinks(userId: String) {
        userRepository.observeLinks(userId) { links ->
            _linksState.value = links?.links?.toMap() ?: emptyMap()
        }
    }
}