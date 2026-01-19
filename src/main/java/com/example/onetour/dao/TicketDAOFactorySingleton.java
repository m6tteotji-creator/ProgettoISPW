package com.example.onetour.dao;

import com.example.onetour.config.AppConfig;

public class TicketDAOFactorySingleton {

    private TicketDAOFactorySingleton() {}

    private static class Helper {
        private static final TicketDAOFactorySingleton INSTANCE = new TicketDAOFactorySingleton();
    }

    public static TicketDAOFactorySingleton getInstance() {
        return Helper.INSTANCE;
    }


    public TicketDAO createTicketDAO() {
        try {
            AppConfig cfg = AppConfig.getInstance();

            // app.mode: DEMO | FULL
            String mode = cfg.get("app.mode", "DEMO").trim();

            // DEMO: force memory ONLY
            if ("DEMO".equalsIgnoreCase(mode)) {
                return new TicketDAOMemory();
            }

            // FULL: choose persistence implementation
            String persistence = cfg.get("ticket.persistence", "CSV").trim();

            return switch (persistence.toUpperCase()) {
                case "JDBC" -> new TicketDAOJDBC();
                case "CSV" -> new TicketDAOCSV();
                case "MEMORY" -> new TicketDAOMemory();
                default -> throw new IllegalArgumentException("Unknown ticket.persistence: " + persistence);
            };

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new FactoryConfigurationException("Unable to create TicketDAO", e);
        }

    }

    public static class FactoryConfigurationException extends RuntimeException {
        public FactoryConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
