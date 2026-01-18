package com.example.onetour.graphiccontrollerCLI;

import java.util.Scanner;

public class NavigatorCLIController {

    protected static final Scanner SC = new Scanner(System.in);

    protected String readLine(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    protected int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println("Scelta non valida. Inserisci un numero tra " + min + " e " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Formato non valido. Inserisci un numero.");
            }
        }
    }
}
