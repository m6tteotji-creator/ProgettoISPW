package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.LoginController;
import com.example.onetour.bean.LoginBean;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.model.Session;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;

public class LoginCLIController extends NavigatorCLIController {

    private final LoginController loginController = new LoginController();

    public void start() {
        CLIPrinter.println("=== ONE TOUR ===");

        while (true) {
            CLIPrinter.println();
            CLIPrinter.println("1) Login");
            CLIPrinter.println("2) Esci");
            int choice = readInt("Seleziona: ", 1, 2);

            if (choice == 2) {
                CLIPrinter.println("Chiusura...");
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
                CLIPrinter.println("Errore: sessione non valida.");
                return;
            }

            RoleEnum role = s.getUser().getRole();
            CLIPrinter.println("Login OK. Ruolo: " + role.getRoleName());
            CLIPrinter.println("SessionID: " + sessionId);

            if (role == RoleEnum.TOURISTGUIDE) {
                new GuideRequestsCLIController(sessionId).start();
            } else {
                new SearchCLIController(sessionId).start();
            }

        } catch (Exception e) {
            CLIPrinter.println("Login fallito: " + e.getMessage());
        }
    }
}
