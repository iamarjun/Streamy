package com.arjun.streamy.util

sealed class Resource<out T : Any> {
    object Loading : Resource<Nothing>()
    data class Success<out T : Any>(private val data: Any) : Resource<T>()
    data class Error(private val e: Exception) : Resource<Nothing>()
}
