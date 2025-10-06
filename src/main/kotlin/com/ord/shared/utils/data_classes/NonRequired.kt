package com.ord.shared.utils.data_classes

data class NonRequired<T>(
    val value: T?,
    val isPresent: Boolean = value != null,
) {
    fun getOrDefault(default: T): T? {
        return if (this.isPresent) value
        else default
    }
}