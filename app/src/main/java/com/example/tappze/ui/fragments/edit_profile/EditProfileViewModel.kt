package com.example.tappze.com.example.tappze.ui.fragments.edit_profile

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
    private val _linksState = MutableStateFlow<Map<String, String>>(emptyMap())
    val linksState: StateFlow<Map<String, String>> = _linksState

    fun uploadImgAndUpdateUser(userData: User) {
        viewModelScope.launch {
            _updateUserState.value = Response.Loading
            _updateUserState.value = userRepository.uploadImageAndUpdateUser(userData)
        }
    }

    fun observeLinks(userId: String) {
        userRepository.observeLinks(userId) { links ->
            _linksState.value = links?.links?.toMap() ?: emptyMap()
        }
    }
}