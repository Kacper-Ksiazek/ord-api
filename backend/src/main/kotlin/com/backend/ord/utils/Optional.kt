package com.backend.ord.utils

data class Optional<T>(
    val value: T?,
    val isPresent: Boolean = value != null,
) {
    fun getOrDefault(default: T): T? {
        return if (this.isPresent) value;
        else default
    }
}