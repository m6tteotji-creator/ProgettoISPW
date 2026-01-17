package com.example.onetour.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private final Properties props = new Properties();

    private AppConfig() {
        try (InputStream is = AppConfig.class.getResourceAsStream("/com/example/onetour/config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.out.println("CONFIG NOT FOUND");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}
