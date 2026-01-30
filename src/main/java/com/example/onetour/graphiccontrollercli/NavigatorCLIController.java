package com.example.onetour.graphiccontrollercli;

import com.example.onetour.util.Printer;

import java.util.Scanner;

public class NavigatorCLIController {

    protected static final Scanner SC = new Scanner(System.in);

    protected String readLine(String prompt) {
        Printer.printMessage(prompt);
        return SC.nextLine().trim();
    }

    protected int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    Printer.printMessage(
                            "Scelta non valida. Inserisci un numero tra " + min + " e " + max + ".\n"
                    );
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                Printer.printMessage("Formato non valido. Inserisci un numero.\n");
            }
        }
    }
}
