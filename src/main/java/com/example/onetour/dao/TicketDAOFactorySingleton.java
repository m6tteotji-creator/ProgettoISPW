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

            // app.mode: DEMO | JDBC
            String mode = cfg.get("app.mode", "DEMO");

            // DEMO
            if ("DEMO".equalsIgnoreCase(mode)) {
                return new TicketDAOMemory();
            }

            // non-DEMO
            String persistence = cfg.get("ticket.persistence", "MYSQL");

            return switch (persistence.toUpperCase()) {
                case "MYSQL" -> new TicketDAOJDBC();
                case "CSV" -> new TicketDAOCSV();
                case "MEMORY" -> new TicketDAOMemory();
                default -> throw new IllegalArgumentException("Unknown ticket.persistence: " + persistence);
            };
        } catch (Exception e) {
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new FactoryConfigurationException("Unable to create TicketDAO", e);
        }
    }

    public static class FactoryConfigurationException extends RuntimeException {
        public FactoryConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}