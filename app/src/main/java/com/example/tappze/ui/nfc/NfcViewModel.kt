package com.example.tappze.com.example.tappze.ui.nfc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NfcViewModel:ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}