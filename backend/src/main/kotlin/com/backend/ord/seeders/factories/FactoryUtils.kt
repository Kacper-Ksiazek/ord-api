package com.backend.ord.seeders.factories

object FactoryUtils {
    fun <T : Enum<T>> getNRandomUniqueValuesFromEnum(enumClass: Class<T>, n: Int): List<T> {
        // Ensure the class is an enum
        require(enumClass.isEnum) { "Class must be an enum" }

        // Get the values of the enum
        val enumConstants = enumClass.enumConstants

        // Validate N
        require(!(n < 1 || n > enumConstants.size)) { "N must be greater than 0 and less than or equal to the number of elements in the enum" }

        // Create a collection of unique values
        val uniqueValues = mutableListOf<T>(*enumConstants).apply { shuffle() }

        // Return a sublist of the first N elements
        return uniqueValues.subList(0, n)
    }
}
