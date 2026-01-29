package com.example.onetour;

import com.example.onetour.config.AppConfig;
import com.example.onetour.enumeration.PersistenceMode;
import com.example.onetour.graphiccontrollercli.LoginCLIController;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainCLI {

    private static final Logger logger = Logger.getLogger(MainCLI.class.getName());

    public static void main(String[] args) {

        PersistenceMode mode = askModeFromConsole();
        AppConfig.getInstance().setPersistenceMode(mode);

        logger.log(Level.INFO, "Persistence mode selected: {0}", mode);

        new LoginCLIController().start();
    }

    private static PersistenceMode askModeFromConsole() {
        try (Scanner sc = new Scanner(System.in)) {

            logger.info("=== ONE TOUR CLI ===");
            logger.info("Select persistence mode:");
            logger.info("1) DEMO");
            logger.info("2) CSV");
            logger.info("3) JDBC");

            while (true) {
                logger.info("Choice (1-3): ");
                String input = sc.nextLine().trim();

                switch (input) {
                    case "1":
                        return PersistenceMode.DEMO;
                    case "2":
                        return PersistenceMode.CSV;
                    case "3":
                        return PersistenceMode.JDBC;
                    default:
                        logger.warning("Invalid choice. Please select 1, 2 or 3.");
                }
            }
        }
    }
}
