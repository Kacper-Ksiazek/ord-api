package com.backend.ord.utils

import com.backend.ord.utils.EnumUtils.getRandomValue
import com.backend.ord.utils.EnumUtils.joinEnumValues
import org.junit.jupiter.api.Test

enum class TestingEnum {
    FIRST,
    SECOND,
    THIRD
}

class TestEnumUtils {
    @Test
    fun `Join enum values with default separator`() {
        val expected = "FIRST, SECOND, THIRD"
        val actual = TestingEnum::class.joinEnumValues()

        assert(expected == actual)
    }

    @Test
    fun `Join enum values with custom separator`() {
        val expected = "FIRST ; SECOND ; THIRD"
        val actual = TestingEnum::class.joinEnumValues(separator = " ; ")

        assert(expected == actual)
    }

    @Test
    fun `Get random enum value`() {
        val randomValue = TestingEnum::class.getRandomValue()

        assert(randomValue in TestingEnum.entries)
    }

    @Test
    fun `All enum values can be randomly selected`() {
        val allValues = TestingEnum.entries
        val notDrawnYetValues = allValues.toMutableSet()

        repeat(allValues.size * 10) {
            val randomValue = TestingEnum::class.getRandomValue()
            notDrawnYetValues.remove(randomValue)
        }

        assert(notDrawnYetValues.isEmpty())
    }
}