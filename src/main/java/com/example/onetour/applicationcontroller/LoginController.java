package com.example.onetour.applicationcontroller;

import com.example.onetour.bean.LoginBean;
import com.example.onetour.config.AppConfig;
import com.example.onetour.dao.LoginDAO;
import com.example.onetour.enumeration.PersistenceMode;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.exception.UserNotFoundException;
import com.example.onetour.model.UserAccount;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;

import java.sql.SQLException;
import java.util.UUID;

public class LoginController {

    public LoginBean login(LoginBean loginBean)
            throws InvalidFormatException, UserNotFoundException, SQLException {

        if (loginBean == null) throw new InvalidFormatException("Invalid login data");
        loginBean.checkFields();

        String email = loginBean.getEmail().trim().toLowerCase();
        String password = loginBean.getPassword();

        PersistenceMode mode = AppConfig.getInstance().getPersistenceMode();

        UserAccount user;
        if (mode == PersistenceMode.JDBC) {
            LoginDAO dao = new LoginDAO();
            user = dao.findPerson(email, password);
        } else {
            RoleEnum role = email.contains("guide") ? RoleEnum.TOURISTGUIDE : RoleEnum.USER;
            user = new UserAccount(UUID.randomUUID().toString(), "Demo", email, role);
        }

        String sessionID = SessionManagerSingleton.getInstance().addSession(user);
        loginBean.setSessionID(sessionID);

        try {
            loginBean.getClass().getMethod("setRole", String.class)
                    .invoke(loginBean, user.getRole().getRoleName());
        } catch (Exception ignored) {
            // intentionally ignored: backward compatibility with old LoginBean
        }

        return loginBean;
    }

    public void logout(LoginBean loginBean) {
        if (loginBean == null) return;
        String sessionID = loginBean.getSessionID();
        if (sessionID == null || sessionID.isBlank()) return;
        SessionManagerSingleton.getInstance().removeSession(sessionID);
    }
}
