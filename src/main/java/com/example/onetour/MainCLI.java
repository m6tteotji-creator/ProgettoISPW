package com.example.onetour;

import com.example.onetour.config.AppConfig;
import com.example.onetour.enumeration.PersistenceMode;
import com.example.onetour.graphiccontrollercli.LoginCLIController;

import java.util.Scanner;

public class MainCLI {

    public static void main(String[] args) {

        PersistenceMode mode = askModeFromConsole();
        AppConfig.getInstance().setPersistenceMode(mode);

        new LoginCLIController().start();
    }

    private static PersistenceMode askModeFromConsole() {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== ONE TOUR CLI ===");
        System.out.println("Select persistence mode:");
        System.out.println("1) DEMO");
        System.out.println("2) CSV");
        System.out.println("3) JDBC");

        while (true) {
            System.out.print("Choice (1-3): ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1":
                    return PersistenceMode.DEMO;
                case "2":
                    return PersistenceMode.CSV;
                case "3":
                    return PersistenceMode.JDBC;
                default:
                    System.out.println("Invalid choice. Please select 1, 2 or 3.");
            }
        }
    }
}
