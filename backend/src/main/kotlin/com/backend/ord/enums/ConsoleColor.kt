package com.backend.ord.enums

enum class ConsoleColor(val ansiCode: String) {
    Blue("\u001B[34m"),
    Red("\u001B[31m"),
    Green("\u001B[32m"),
    Yellow("\u001B[33m"),
    Purple("\u001B[35m"),
    Cyan("\u001B[36m"),
    Reset("\u001B[0m")
}
