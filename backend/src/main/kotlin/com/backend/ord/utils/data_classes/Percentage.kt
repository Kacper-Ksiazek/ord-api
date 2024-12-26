package com.backend.ord.utils.data_classes

class Percentage(
    /** Value of the percentage. It must be between 0.0 and 100.0 */
    val value: Double
) {
    init {
        require(value in 0.0..100.0) { "The value of the percentage must be between 0 and 100" }
    }

    override fun toString(): String {
        return "$value%"
    }

    operator fun plus(other: Percentage): Percentage {
        return Percentage(value + other.value)
    }

    operator fun minus(other: Percentage): Percentage {
        return Percentage(value - other.value)
    }

    operator fun times(other: Percentage): Percentage {
        return Percentage((value / 100) * (other.value / 100) * 100)
    }

    operator fun div(other: Percentage): Percentage {
        require(other.value != 0.0) { "Cannot divide by zero percentage" }

        return Percentage(value / other.value * 100)
    }

    operator fun times(other: Int): Double {
        return (value / 100) * other
    }

    operator fun times(other: Double): Double {
        return (value / 100) * other
    }

    operator fun times(other: Float): Double {
        return (value / 100) * other
    }

    operator fun times(other: Long): Double {
        return (value / 100) * other
    }
}

