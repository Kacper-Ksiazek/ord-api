package com.backend.ord.utils;

import com.backend.ord.enums.ConsoleColor;

import java.util.function.Function;
import java.util.stream.IntStream;

public final class Console {
    private Console() {
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static void print(String message, ConsoleColor color) {
        String msg = String.format("%s%s%s", color.getANSICode(), message, ConsoleColor.Reset.getANSICode());
        System.out.print(msg);
    }

    public static void printBlue(String message) {
        print(message, ConsoleColor.Blue);
    }

    public static void printRed(String message) {
        print(message, ConsoleColor.Red);
    }

    public static void printGreen(String message) {
        print(message, ConsoleColor.Green);
    }

    public static void printYellow(String message) {
        print(message, ConsoleColor.Yellow);
    }

    public static void printPurple(String message) {
        print(message, ConsoleColor.Purple);
    }

    public static void printCyan(String message) {
        print(message, ConsoleColor.Cyan);
    }

    public static void addBreakLine(int lines) {
        IntStream.range(0, lines).forEach(i -> System.out.println());
    }

    public static void ensureFunctionSuccess(String introMsg, Runnable function) {
        print(introMsg);
        try {
            function.run();
            Console.printGreen(" ✅ DONE\n");
        } catch (Exception e) {
            Console.printRed(" ❌ ERROR\n");
        }
    }
}
