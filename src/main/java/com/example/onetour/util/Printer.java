package com.example.onetour.util;

public class Printer {
    private Printer() {
        throw new IllegalStateException("Utility class");
    }

    public static void printMessage(String s) {
        System.out.print(s);
    }
}
