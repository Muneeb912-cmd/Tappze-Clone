package com.example.tappze.com.example.tappze.ui.add_edit_link

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditLinkViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _saveLinksState = MutableStateFlow<Response<Unit>>(Response.Loading)
    val saveLinksState: StateFlow<Response<Unit>> = _saveLinksState


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
}
