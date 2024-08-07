package com.example.tappze.com.example.tappze.ui.fragments.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tappze.com.example.tappze.models.User
import com.example.tappze.com.example.tappze.repository.UserAuthentication
import com.example.tappze.repository.UserRepository
import com.example.tappze.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignInViewModel@Inject constructor(
    private val auth: UserAuthentication
) : ViewModel() {

    private val _signInState = MutableStateFlow<Response<User>>(Response.Loading)
    val signInState: StateFlow<Response<User>> = _signInState

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _signInState.value = Response.Loading
            when (val response = auth.signInWithEmailPassword(email, password)) {
                is Response.Success -> {
                    _userData.value = response.data
                    _signInState.value = response
                }

                is Response.Error -> _signInState.value = response
                Response.Loading -> TODO()
            }
        }
    }
}