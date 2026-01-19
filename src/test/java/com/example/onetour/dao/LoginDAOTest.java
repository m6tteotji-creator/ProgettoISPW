package com.example.onetour.dao;

import com.example.onetour.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Person in charge: Matteo
 */
class LoginDAOTest {

    @Test
    void loginWrongUser() {
        assertThrows(UserNotFoundException.class, () -> {
            String email = "test@test.com";
            String password = "wrong";
            LoginDAO loginDAO = new LoginDAO();

            try {
                loginDAO.findPerson(email, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
