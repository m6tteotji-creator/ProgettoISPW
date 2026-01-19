package com.example.onetour.pattern;

import com.example.onetour.dao.TicketDAO;
import com.example.onetour.dao.TicketDAOFactorySingleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Person in charge: Matteo
 */
class TicketDAOFactorySingletonTest {

    @Test
    void ticketDAORight() {
        assertDoesNotThrow(() -> {
            TicketDAOFactorySingleton ticketDAOFactory =
                    TicketDAOFactorySingleton.getInstance();

            TicketDAO ticketDAO = ticketDAOFactory.createTicketDAO();
        }, "Creating TicketDAO should not throw any exception");
    }
}
