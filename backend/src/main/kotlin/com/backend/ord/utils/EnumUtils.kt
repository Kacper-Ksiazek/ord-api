package com.backend.ord.utils

import kotlin.reflect.KClass

object EnumUtils {
    /**
     * Joins the names of all enum constants in the specified enum class into a single string.
     *
     * This extension function is applied to a `KClass` representing an enum type.
     * It returns a string containing the names of all the enum constants, separated by the specified delimiter.
     *
     * @param separator The string used to separate each enum constant name in the resulting string.
     *                  The default value is ", ".
     * @return A string that contains the names of all enum constants in the enum class, separated by the given separator.
     *
     * @param T The type of the enum class.
     *
     * @sample
     * enum class Color { RED, GREEN, BLUE }
     *
     * val result = Color::class.joinEnumValues(" | ")
     * // result: "RED | GREEN | BLUE"
     */
    fun <T : Enum<T>> KClass<T>.joinEnumValues(separator: String = ", "): String {
        return this.java.enumConstants.joinToString(separator) { it.name }
    }

    /**
     * Returns a random enum constant from the specified enum class.
     *
     * This extension function is applied to a `KClass` representing an enum type.
     * It randomly selects one of the enum constants and returns it.
     *
     * @return A random enum constant from the enum class.
     *
     * @param T The type of the enum class.
     *
     * @sample
     * enum class Color { RED, GREEN, BLUE }
     *
     * val randomColor = Color::class.getRandomValue()
     * // randomColor: could be RED, GREEN, or BLUE
     */
    fun <T : Enum<T>> KClass<T>.getRandomValue(): T {
        return this.java.enumConstants.random()
    }
}