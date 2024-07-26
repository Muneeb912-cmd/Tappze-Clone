package com.example.tappze.utils



sealed class Response<out T> {
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val exception: Throwable) : Response<Nothing>()
    data object Loading : Response<Nothing>()
}
