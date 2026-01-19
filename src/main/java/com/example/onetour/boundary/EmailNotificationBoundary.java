package com.example.onetour.boundary;

import com.example.onetour.bean.EmailBean;
import com.example.onetour.config.AppConfig;
import com.example.onetour.enumeration.TicketState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailNotificationBoundary {

    private static final Logger logger = Logger.getLogger(EmailNotificationBoundary.class.getName());

    private static final String DATA_DIR =
            System.getProperty("user.home") + File.separator + "onetour-data";
    private static final String FILE_PATH =
            DATA_DIR + File.separator + "email_notifications.txt";

    public void sendNotification(EmailBean emailBean) {
        if (emailBean == null) {
            logger.warning("EmailBean is null");
            return;
        }

        String decisionText = mapDecision(emailBean.getDecision());

        String mode = AppConfig.getInstance().get("app.mode", "DEMO").trim();
        if ("DEMO".equalsIgnoreCase(mode)) {
            logger.log(
                    Level.INFO,
                    "[DEMO EMAIL] TO={0} FROM={1} TOUR_ID={2} DECISION={3}",
                    new Object[] {
                            emailBean.getUserEmail(),
                            emailBean.getGuideEmail(),
                            emailBean.getTourID(),
                            decisionText
                    }
            );
            return;
        }

        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            logger.log(Level.SEVERE, "Unable to create data directory: {0}", DATA_DIR);
            return;
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
                emailBean.getGuideEmail(),
                emailBean.getUserEmail(),
                emailBean.getTourID(),
                decisionText
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(message);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing email notification", e);
        }
    }

    private String mapDecision(TicketState decision) {
        if (decision == null) {
            return "processed";
        }

        return switch (decision) {
            case CONFIRMED -> "ACCEPTED";
            case REJECTED -> "REJECTED";
            case PENDING -> "RECEIVED";
        };
    }
}
