package com.example.onetour.graphiccontrollerCLI;

import com.example.onetour.applicationcontroller.LoginController;
import com.example.onetour.bean.LoginBean;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.model.Session;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;

public class LoginCLIController extends NavigatorCLIController {

    private final LoginController loginController = new LoginController();

    public void start() {
        System.out.println("=== ONE TOUR ===");

        while (true) {
            System.out.println();
            System.out.println("1) Login");
            System.out.println("2) Esci");
            int choice = readInt("Seleziona: ", 1, 2);

            if (choice == 2) {
                System.out.println("Chiusura...");
                return;
            }

            doLogin();
        }
    }

    private void doLogin() {
        try {
            String email = readLine("Email: ");
            String password = readLine("Password: ");

            LoginBean bean = new LoginBean();
            bean.setEmail(email);
            bean.setPassword(password);

            LoginBean logged = loginController.login(bean);

            String sessionId = logged.getSessionID();
            Session s = SessionManagerSingleton.getInstance().getSession(sessionId);
            if (s == null || s.getUser() == null) {
                System.out.println("Errore: sessione non valida.");
                return;
            }

            RoleEnum role = s.getUser().getRole();
            System.out.println("Login OK. Ruolo: " + role.getRoleName());
            System.out.println("SessionID: " + sessionId);

            if (role == RoleEnum.TOURISTGUIDE) {
                new GuideRequestsCLIController(sessionId).start();
            } else {
                new SearchCLIController(sessionId).start();
            }

        } catch (Exception e) {
            System.out.println("Login fallito: " + e.getMessage());
        }
    }
}
