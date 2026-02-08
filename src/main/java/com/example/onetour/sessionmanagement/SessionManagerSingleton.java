package com.example.onetour.sessionmanagement;

import com.example.onetour.model.Session;
import com.example.onetour.model.UserAccount;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionManagerSingleton {

    private static final Logger logger = Logger.getLogger(SessionManagerSingleton.class.getName());
    private final Map<String, Session> activeSessions = new ConcurrentHashMap<>();

    private volatile String currentSessionID;

    private SessionManagerSingleton() {
        // singleton
    }

    private static class Helper {
        private static final SessionManagerSingleton INSTANCE = new SessionManagerSingleton();
    }

    public static SessionManagerSingleton getInstance() {
        return Helper.INSTANCE;
    }

    public String addSession(UserAccount user) {
        if (user == null) throw new IllegalArgumentException("User is null");
        String email = normalizeEmail(user.getUserEmail());
        if (email.isBlank()) throw new IllegalArgumentException("User email is null/blank");

        Session existing = findSessionByEmail(email);
        if (existing != null) {
            currentSessionID = existing.getSessionID();
            logger.log(Level.INFO, "Reusing existing session for email: {0}", email);
            return existing.getSessionID();
        }

        String sessionID = UUID.randomUUID().toString();
        Session session = new Session(sessionID, user);
        activeSessions.put(sessionID, session);

        currentSessionID = sessionID;
        return sessionID;
    }

    public void removeSession(String sessionID) {
        if (sessionID == null || sessionID.isBlank()) return;

        activeSessions.remove(sessionID);

        String current = currentSessionID;
        if (current != null && current.equals(sessionID)) {
            currentSessionID = null;
        }
    }

    public Session getSession(String sessionID) {
        if (sessionID == null || sessionID.isBlank()) return null;
        return activeSessions.get(sessionID);
    }

    public Session getCurrentSession() {
        String current = currentSessionID;
        if (current == null) return null;
        return activeSessions.get(current);
    }

    public void clearCurrentSession() {
        currentSessionID = null;
    }

    private Session findSessionByEmail(String normalizedEmail) {
        return activeSessions.values()
                .stream()
                .filter(s -> s.getUser() != null
                        && normalizeEmail(s.getUser().getUserEmail()).equals(normalizedEmail))
                .findFirst()
                .orElse(null);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
