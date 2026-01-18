package com.example.onetour.graphiccontrollercli;

import java.util.Scanner;

public class NavigatorCLIController {

    protected static final Scanner SC = new Scanner(System.in);

    protected String readLine(String prompt) {
        CLIPrinter.print(prompt);
        return SC.nextLine().trim();
    }

    protected int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    CLIPrinter.println("Scelta non valida. Inserisci un numero tra " + min + " e " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                CLIPrinter.println("Formato non valido. Inserisci un numero.");
            }
        }
    }
}
