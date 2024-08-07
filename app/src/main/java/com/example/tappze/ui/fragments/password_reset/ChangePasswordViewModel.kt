package com.example.tappze.com.example.tappze.ui.fragments.password_reset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tappze.com.example.tappze.repository.UserAuthentication
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val auth: UserAuthentication
) : ViewModel() {

    private val _passwordResetResult = MutableStateFlow<Response<Unit>>(Response.Loading)
    val passwordResetResult: StateFlow<Response<Unit>> get() = _passwordResetResult

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            val response = auth.sendChangePasswordEmail(email)
            _passwordResetResult.value = response
        }
    }
}
