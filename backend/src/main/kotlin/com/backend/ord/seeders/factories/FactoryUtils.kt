package com.backend.ord.seeders.factories

import java.util.*

object FactoryUtils {
    fun <T : Enum<T>?> getNRandomUniqueValuesFromEnum(enumClass: Class<T>, N: Int): List<T?> {
        // Ensure the class is an enum
        require(enumClass.isEnum) { "Class must be an enum" }

        // Get the values of the enum
        val enumConstants = enumClass.enumConstants

        // Validate N
        require(!(N < 1 || N > enumConstants.size)) { "N must be greater than 0 and less than or equal to the number of elements in the enum" }

        // Create a collection of unique values
        val uniqueValues: List<T?> = ArrayList()
        Collections.addAll(uniqueValues, *enumConstants)

        // Shuffle the collection
        Collections.shuffle(uniqueValues)

        // Return a sublist of the first N elements
        return uniqueValues.subList(0, N)
    }
}
