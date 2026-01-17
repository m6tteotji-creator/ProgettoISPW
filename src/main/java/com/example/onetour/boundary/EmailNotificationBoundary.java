package com.example.onetour.boundary;

import com.example.onetour.bean.EmailBean;
import com.example.onetour.enumeration.TicketState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailNotificationBoundary {

    private static final String FILE_PATH = "email_notifications.txt";


    private static final Logger logger =
            Logger.getLogger(EmailNotificationBoundary.class.getName());


    public void sendNotification(EmailBean emailBean) {
        if (emailBean == null) {
            logger.log(Level.WARNING, "EmailBean is null");
            return;
        }

        String decisionText = mapDecision(emailBean.getDecision());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {

            writer.write("=====================================\n");
            writer.write("DATE: " + LocalDateTime.now() + "\n");
            writer.write("FROM (Guide): " + emailBean.getGuideEmail() + "\n");
            writer.write("TO (User): " + emailBean.getUserEmail() + "\n");
            writer.write("TOUR ID: " + emailBean.getTourID() + "\n");
            writer.write("-------------------------------------\n");
            writer.write("Your booking request has been " + decisionText + ".\n");
            writer.write("=====================================\n\n");

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

