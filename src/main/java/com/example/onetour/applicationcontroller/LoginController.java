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

public class LoginController {

    private static final String GTEST_EMAIL = "guide@test.com";
    private static final String G1_EMAIL = "guide1@guide.com";
    private static final String G2_EMAIL = "guide2@guide.com";
    private static final String G3_EMAIL = "guide3@guide.com";
    private static final String G4_EMAIL = "guide4@guide.com";

    public LoginBean login(LoginBean loginBean)
            throws InvalidFormatException, UserNotFoundException, SQLException {

        if (loginBean == null) throw new InvalidFormatException("Invalid login data");
        loginBean.checkFields();

        String email = normalizeEmail(loginBean.getEmail());
        String password = loginBean.getPassword();

        PersistenceMode mode = AppConfig.getInstance().getPersistenceMode();

        UserAccount user;
        if (mode == PersistenceMode.JDBC) {
            LoginDAO dao = new LoginDAO();
            user = dao.findPerson(email, password);
        } else {
            user = loginNoDb(email, password);
        }

        String sessionID = SessionManagerSingleton.getInstance().addSession(user);
        loginBean.setSessionID(sessionID);

        try {
            loginBean.getClass().getMethod("setRole", String.class)
                    .invoke(loginBean, user.getRole().getRoleName());
        } catch (Exception ignored) {
            // backward compatibility with old LoginBean
        }

        return loginBean;
    }

    private UserAccount loginNoDb(String email, String password) throws UserNotFoundException {
        email = normalizeEmail(email);
        if (password == null) password = "";

        AppConfig cfg = AppConfig.getInstance();

        String demoUserEmail = normalizeEmail(cfg.getDemoUserEmail());
        String demoUserPw = cfg.getDemoUserPassword();

        // Tourist demo
        if (demoUserEmail.equals(email) && demoUserPw != null && demoUserPw.equals(password)) {
            return new UserAccount("U000", "User", demoUserEmail, RoleEnum.USER);
        }

        // Guides demo
        String demoGuidePw = cfg.getDemoGuidePassword();
        if (demoGuidePw != null && demoGuidePw.equals(password)) {
            if (GTEST_EMAIL.equals(email)) return new UserAccount("G000", "Guida", GTEST_EMAIL, RoleEnum.TOURISTGUIDE);
            if (G1_EMAIL.equals(email))    return new UserAccount("G001", "Marco", G1_EMAIL, RoleEnum.TOURISTGUIDE);
            if (G2_EMAIL.equals(email))    return new UserAccount("G002", "Luca", G2_EMAIL, RoleEnum.TOURISTGUIDE);
            if (G3_EMAIL.equals(email))    return new UserAccount("G003", "Anna", G3_EMAIL, RoleEnum.TOURISTGUIDE);
            if (G4_EMAIL.equals(email))    return new UserAccount("G004", "Sofia", G4_EMAIL, RoleEnum.TOURISTGUIDE);
        }

        throw new UserNotFoundException(
                "Invalid credentials (DEMO/CSV mode). " +
                        "Tourist email: " + cfg.getDemoUserEmail() + ". " +
                        "Guide emails: " + GTEST_EMAIL + ", " + G1_EMAIL + ", " + G2_EMAIL + ", " + G3_EMAIL + ", " + G4_EMAIL + ". " +
                        "Use the demo passwords configured in config.properties."
        );
    }

    private String normalizeEmail(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    public void logout(LoginBean loginBean) {
        if (loginBean == null) return;
        String sessionID = loginBean.getSessionID();
        if (sessionID == null || sessionID.isBlank()) return;
        SessionManagerSingleton.getInstance().removeSession(sessionID);
    }
}
