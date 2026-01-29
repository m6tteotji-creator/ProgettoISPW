package com.example.onetour.config;

import com.example.onetour.enumeration.PersistenceMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfig {

    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());

    private final Properties props = new Properties();

    private volatile PersistenceMode runtimeMode = null;

    private AppConfig() {
        try (InputStream is = AppConfig.class.getResourceAsStream(
                "/com/example/onetour/config.properties")) {

            if (is != null) {
                props.load(is);
            } else {
                logger.log(Level.WARNING, "CONFIG NOT FOUND");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading configuration", e);
        }
    }

    private static class Helper {
        private static final AppConfig INSTANCE = new AppConfig();
    }

    public static AppConfig getInstance() {
        return Helper.INSTANCE;
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    // ---------------------------
    // Runtime Persistence Mode
    // ---------------------------

    public void setPersistenceMode(PersistenceMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("PersistenceMode cannot be null");
        }
        this.runtimeMode = mode;
    }

    public PersistenceMode getPersistenceMode() {
        if (runtimeMode == null) {
            throw new IllegalStateException(
                    "PersistenceMode not set. You must select DEMO / CSV / JDBC at startup."
            );
        }
        return runtimeMode;
    }

    // ---------------------------
    // DEMO / CSV credentials
    // ---------------------------

    public String getDemoUserEmail() {
        return get("demo.user.email", "user@user.com").trim().toLowerCase();
    }


    public String getDemoUserPassword() {
        return get("demo.user.password", "pw123");
    }

    public String getDemoGuidePassword() {
        return get("demo.guide.password", "pw123");
    }
}
