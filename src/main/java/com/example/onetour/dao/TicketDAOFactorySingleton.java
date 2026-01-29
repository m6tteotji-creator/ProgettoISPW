package com.example.onetour.dao;

import com.example.onetour.config.AppConfig;
import com.example.onetour.enumeration.PersistenceMode;

public class TicketDAOFactorySingleton {

    private TicketDAOFactorySingleton() {}

    private static class Helper {
        private static final TicketDAOFactorySingleton INSTANCE = new TicketDAOFactorySingleton();
    }

    public static TicketDAOFactorySingleton getInstance() {
        return Helper.INSTANCE;
    }

    public TicketDAO createTicketDAO() {

        PersistenceMode mode = AppConfig.getInstance().getPersistenceMode();

        return switch (mode) {
            case DEMO -> new TicketDAOMemory();
            case CSV  -> new TicketDAOCSV();
            case JDBC -> new TicketDAOJDBC();
        };
    }
}
