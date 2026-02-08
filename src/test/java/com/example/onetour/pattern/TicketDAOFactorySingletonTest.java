package com.example.onetour.pattern;

import com.example.onetour.config.AppConfig;
import com.example.onetour.dao.TicketDAO;
import com.example.onetour.dao.TicketDAOFactorySingleton;
import com.example.onetour.enumeration.PersistenceMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Person in charge: Matteo
 */
class TicketDAOFactorySingletonTest {

    @Test
    void ticketDAORight() {
        AppConfig.getInstance().setPersistenceMode(PersistenceMode.DEMO);

        assertDoesNotThrow(() -> {
            TicketDAOFactorySingleton factory = TicketDAOFactorySingleton.getInstance();
            TicketDAO ticketDAO = factory.createTicketDAO();
            assertNotNull(ticketDAO);
        }, "Creating TicketDAO should not throw any exception");
    }
}
