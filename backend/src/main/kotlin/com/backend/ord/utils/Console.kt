package com.backend.ord.utils



object Console {
    enum class Color(val ansiCode: String) {
        Blue("\u001B[34m"),
        Red("\u001B[31m"),
        Green("\u001B[32m"),
        Yellow("\u001B[33m"),
        Purple("\u001B[35m"),
        Cyan("\u001B[36m"),
        Reset("\u001B[0m")
    }

    fun print(message: String?) {
        kotlin.io.print(message)
    }

    fun print(message: String?, color: Color) {
        val msg = "${color.ansiCode}$message${Color.Reset.ansiCode}"
        kotlin.io.print(msg)
    }

    fun printBlue(message: String?) = print(message, Color.Blue)
    fun printRed(message: String?) = print(message, Color.Red)
    fun printGreen(message: String?) = print(message, Color.Green)
    fun printYellow(message: String?) = print(message, Color.Yellow)
    fun printPurple(message: String?) = print(message, Color.Purple)
    fun printCyan(message: String?) = print(message, Color.Cyan)

    fun addBreakLine(lines: Int) {
        repeat(lines) { println() }
    }

    fun ensureFunctionSuccess(introMsg: String?, function: () -> Any) {
        print(introMsg)
        try {
            val outroMsg = function()
            printGreen(" ✅ DONE\n")

            if (outroMsg is String) {
                println(outroMsg)
            }
        } catch (e: Exception) {
            printRed(" ❌ ERROR\n")

            throw e
        }
    }

    fun printWithMargin(message: String?) {
        addBreakLine(2)
        print(message)
        addBreakLine(2)
    }
}
