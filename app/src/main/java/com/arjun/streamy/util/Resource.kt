package com.arjun.streamy.util

sealed class Resource {
    object Loading : Resource()
    data class Success(private val data: Any) : Resource()
    data class Error(private val e: Exception) : Resource()
}
