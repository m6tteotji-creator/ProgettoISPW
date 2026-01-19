package com.example.onetour.bean;

import com.example.onetour.exception.InvalidFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Person in charge: Matteo
 */

class LoginBeanTest {

    @Test
    void wrongField() {
        assertThrows(InvalidFormatException.class, () -> {
            LoginBean loginBean = new LoginBean();
            loginBean.setEmail("");
            loginBean.setPassword("password");
            loginBean.checkFields();
        });
    }

    @Test
    void rightField() {
        LoginBean loginBean = new LoginBean();
        loginBean.setEmail("email@test.com");
        loginBean.setPassword("password");
        assertDoesNotThrow(loginBean::checkFields);
    }
}
