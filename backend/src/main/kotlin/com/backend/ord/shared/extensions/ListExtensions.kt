package com.backend.ord.shared.extensions

import com.backend.ord.exceptions.REST.BadRequestException

/**
 * Converts a list to a set explicitly, meaning that it will throw an exception if the list contains duplicates.
 */
fun <T> List<T>.convertToSetExplicitly(
    paramName: String? = null
): Set<T> {
    if (this.size != this.toSet().size) {
        if (paramName?.isNotBlank() == true) {
            throw BadRequestException("Give parameter $paramName contains duplicates")
        } else {
            throw IllegalStateException("List contains duplicates")
        }
    }

    return this.toSet()
}