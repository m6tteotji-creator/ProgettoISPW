package com.example.onetour.boundary;

import com.example.onetour.bean.EmailBean;
import com.example.onetour.enumeration.TicketState;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailNotificationBoundary {

    private static final Path BASE_DIR =
            Path.of(System.getProperty("user.home"), "onetour-data");

    private static final Path EMAIL_FILE =
            BASE_DIR.resolve("email_notifications.txt");

    private static final Logger logger =
            Logger.getLogger(EmailNotificationBoundary.class.getName());

    public void sendNotification(EmailBean emailBean) {
        if (emailBean == null) {
            logger.log(Level.WARNING, "EmailBean is null");
            return;
        }

        String decisionText = mapDecision(emailBean.getDecision());

        try {
            Files.createDirectories(BASE_DIR);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    EMAIL_FILE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {
                writer.write("=====================================");
                writer.newLine();
                writer.write("DATE: " + LocalDateTime.now());
                writer.newLine();
                writer.write("FROM (Guide): " + emailBean.getGuideEmail());
                writer.newLine();
                writer.write("TO (User): " + emailBean.getUserEmail());
                writer.newLine();
                writer.write("TOUR ID: " + emailBean.getTourID());
                writer.newLine();
                writer.write("-------------------------------------");
                writer.newLine();
                writer.write("Your booking request has been " + decisionText + ".");
                writer.newLine();
                writer.write("=====================================");
                writer.newLine();
                writer.newLine();
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing email notification", e);
        }
    }

    private String mapDecision(TicketState decision) {
        if (decision == null) return "processed";

        return switch (decision) {
            case CONFIRMED -> "ACCEPTED";
            case REJECTED -> "REJECTED";
            case PENDING -> "RECEIVED";
        };
    }
}
