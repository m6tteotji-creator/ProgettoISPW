package com.example.onetour.boundary;

import com.example.onetour.bean.EmailBean;
import com.example.onetour.config.AppConfig;
import com.example.onetour.enumeration.PersistenceMode;
import com.example.onetour.enumeration.TicketState;
import com.example.onetour.util.Printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class EmailNotificationBoundary {

    private static final String DATA_DIR =
            System.getProperty("user.home") + File.separator + "onetour-data";
    private static final String FILE_PATH =
            DATA_DIR + File.separator + "email_notifications.txt";

    public void sendNotification(EmailBean emailBean) {
        if (emailBean == null) return;

        String decisionText = mapDecision(emailBean.getDecision());
        PersistenceMode mode = AppConfig.getInstance().getPersistenceMode();
        
        if (mode == PersistenceMode.DEMO) {
            Printer.printMessage("\n");
            Printer.printMessage("=== NOTIFICA EMAIL (DEMO) ===\n");
            Printer.printMessage("Da (Guida): " + safe(emailBean.getGuideEmail()) + "\n");
            Printer.printMessage("A (Utente): " + safe(emailBean.getUserEmail()) + "\n");
            Printer.printMessage("Tour ID: " + safe(emailBean.getTourID()) + "\n");
            Printer.printMessage("Esito: " + decisionText + "\n");
            Printer.printMessage("============================\n");
            return;
        }
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String message = String.format(
                "=====================================%n" +
                        "DATE: %s%n" +
                        "FROM (Guide): %s%n" +
                        "TO (User): %s%n" +
                        "TOUR ID: %s%n" +
                        "-------------------------------------%n" +
                        "Your booking request has been %s.%n" +
                        "=====================================%n%n",
                LocalDateTime.now(),
                safe(emailBean.getGuideEmail()),
                safe(emailBean.getUserEmail()),
                safe(emailBean.getTourID()),
                decisionText
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(message);
        } catch (IOException ignored) {
            // silent failure: boundary simulates an external service
        }
    }

    private String mapDecision(TicketState decision) {
        if (decision == null) return "PROCESSED";

        return switch (decision) {
            case CONFIRMED -> "ACCEPTED";
            case REJECTED -> "REJECTED";
            case PENDING -> "RECEIVED";
        };
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
