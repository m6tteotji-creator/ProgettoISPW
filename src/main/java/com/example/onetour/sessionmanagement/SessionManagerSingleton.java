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
    private final Map<String, Session> activeSessions;

    private volatile String currentSessionID;

    private SessionManagerSingleton() {
        this.activeSessions = new ConcurrentHashMap<>();
    }

    private static class Helper {
        private static final SessionManagerSingleton INSTANCE = new SessionManagerSingleton();
    }

    public static SessionManagerSingleton getInstance() {
        return Helper.INSTANCE;
    }

    public String addSession(UserAccount user) {
        if (user == null || user.getUserEmail() == null) {
            throw new IllegalArgumentException("User or email is null");
        }

        Session existing = findSessionByEmail(user.getUserEmail());
        if (existing != null) {
            logger.log(Level.WARNING, "Session already exists for email: {0}", user.getUserEmail());
            currentSessionID = existing.getSessionID();
            return existing.getSessionID();
        }

        String sessionID = UUID.randomUUID().toString();
        Session session = new Session(sessionID, user);
        activeSessions.put(sessionID, session);

        currentSessionID = sessionID;

        return sessionID;
    }

    public void removeSession(String sessionID) {
        if (sessionID == null) return;
        activeSessions.remove(sessionID);

        if (sessionID.equals(currentSessionID)) {
            currentSessionID = null;
        }
    }

    public Session getSession(String sessionID) {
        if (sessionID == null) return null;
        return activeSessions.get(sessionID);
    }

    public Session getCurrentSession() {
        if (currentSessionID == null) return null;
        return activeSessions.get(currentSessionID);
    }

    public void clearCurrentSession() {
        currentSessionID = null;
    }

    private Session findSessionByEmail(String email) {
        String normalized = email.trim().toLowerCase();
        return activeSessions.values()
                .stream()
                .filter(s -> s.getUser() != null
                        && s.getUser().getUserEmail() != null
                        && s.getUser().getUserEmail().trim().toLowerCase().equals(normalized))
                .findFirst()
                .orElse(null);
    }
}
