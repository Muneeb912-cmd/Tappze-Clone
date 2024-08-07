package com.example.tappze.com.example.tappze.ui.fragments.sign_up

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
class SignUpViewModel @Inject constructor(
    private val auth: UserAuthentication
) : ViewModel() {
    private val _signUpState = MutableStateFlow<Response<Unit>>(Response.Loading)
    val signUpState: StateFlow<Response<Unit>> = _signUpState

    fun signUpWithEmailPassword(email: String, password: String, name: String, userName: String) {
        viewModelScope.launch {
            _signUpState.value = Response.Loading
            _signUpState.value =
                auth.signUpWithEmailPassword(email, password, name, userName)
        }
    }

    fun validateFields(
        name: String,
        userName: String,
        email: String,
        password: String
    ): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()
        if (name.isEmpty()) {
            errors["name"] = "Name cannot be empty"
        }
        if (userName.isEmpty()) {
            errors["userName"] = "Username cannot be empty"
        } else if (!isUserNameValid(userName)) {
            errors["userName"] = "Username can only contain lowercase letters and digits"
        }
        if (email.isEmpty()) {
            errors["email"] = "Email cannot be empty"
        }
        if (password.isEmpty()) {
            errors["password"] = "Password cannot be empty"
        } else if (!isPasswordValid(password)) {
            errors["password"] = "Password must be at least 6 characters"
        }

        return errors
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun isUserNameValid(userName: String): Boolean {
        val regex = "^[a-z0-9]+$".toRegex()
        return regex.matches(userName)
    }
}