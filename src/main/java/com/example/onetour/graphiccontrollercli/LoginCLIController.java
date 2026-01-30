package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.LoginController;
import com.example.onetour.bean.LoginBean;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.model.Session;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;
import com.example.onetour.util.Printer;

public class LoginCLIController extends NavigatorCLIController {

    private final LoginController loginController = new LoginController();

    public void start() {
        Printer.printMessage("=== ONE TOUR ===\n");

        while (true) {
            Printer.printMessage("\n");
            Printer.printMessage("1) Login\n");
            Printer.printMessage("2) Esci\n");
            int choice = readInt("Seleziona: ", 1, 2);

            if (choice == 2) {
                Printer.printMessage("Chiusura...\n");
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
                Printer.printMessage("Errore: sessione non valida.\n");
                return;
            }

            RoleEnum role = s.getUser().getRole();
            Printer.printMessage("Login OK. Ruolo: " + role.getRoleName() + "\n");
            Printer.printMessage("SessionID: " + sessionId + "\n");

            if (role == RoleEnum.TOURISTGUIDE) {
                new GuideRequestsCLIController(sessionId).start();
            } else {
                new SearchCLIController(sessionId).start();
            }

        } catch (Exception e) {
            Printer.printMessage("Login fallito: " + e.getMessage() + "\n");
        }
    }
}
