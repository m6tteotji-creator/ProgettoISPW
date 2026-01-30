package com.example.onetour;

import com.example.onetour.config.AppConfig;
import com.example.onetour.enumeration.PersistenceMode;
import com.example.onetour.graphiccontrollercli.LoginCLIController;
import com.example.onetour.util.Printer;

import java.util.Scanner;

public class MainCLI {

    public static void main(String[] args) {

        PersistenceMode mode = askModeFromConsole();
        AppConfig.getInstance().setPersistenceMode(mode);

        new LoginCLIController().start();
    }

    private static PersistenceMode askModeFromConsole() {
        Scanner sc = new Scanner(System.in);

        Printer.printMessage("=== ONE TOUR CLI ===\n");
        Printer.printMessage("Select persistence mode:\n");
        Printer.printMessage("1) DEMO\n");
        Printer.printMessage("2) CSV\n");
        Printer.printMessage("3) JDBC\n");

        while (true) {
            Printer.printMessage("Choice (1-3): ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1":
                    return PersistenceMode.DEMO;
                case "2":
                    return PersistenceMode.CSV;
                case "3":
                    return PersistenceMode.JDBC;
                default:
                    Printer.printMessage("Invalid choice. Please select 1, 2 or 3.\n");
            }
        }
    }
}
