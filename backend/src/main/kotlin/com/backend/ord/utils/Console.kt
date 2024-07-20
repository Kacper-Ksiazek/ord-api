package com.backend.ord.utils

import com.backend.ord.enums.ConsoleColor
import kotlin.reflect.KFunction

object Console {
    fun print(message: String?) {
        kotlin.io.print(message)
    }

    fun print(message: String?, color: ConsoleColor) {
        val msg = "${color.ansiCode}$message${ConsoleColor.Reset.ansiCode}"
        kotlin.io.print(msg)
    }

    fun printBlue(message: String?) = print(message, ConsoleColor.Blue)
    fun printRed(message: String?) = print(message, ConsoleColor.Red)
    fun printGreen(message: String?) = print(message, ConsoleColor.Green)
    fun printYellow(message: String?) = print(message, ConsoleColor.Yellow)
    fun printPurple(message: String?) = print(message, ConsoleColor.Purple)
    fun printCyan(message: String?) = print(message, ConsoleColor.Cyan)

    fun addBreakLine(lines: Int) {
        repeat(lines) { println() }
    }

    fun ensureFunctionSuccess(introMsg: String?, function: () -> Any) {
        print(introMsg)
        try {
            val outroMsg = function();
            printGreen(" ✅ DONE\n")

            if (outroMsg is String) {
                print(outroMsg)
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
