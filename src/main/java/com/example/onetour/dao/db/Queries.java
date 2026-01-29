package com.example.onetour.dao.db;

public final class Queries {

    private Queries() {}

    // =========================
    // TICKETS
    // =========================

    public static final String INSERT_TICKET =
            "INSERT INTO tickets (ticket_id, state, booking_date, user_email, tour_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    public static final String SELECT_TICKETS_BY_USER =
            "SELECT ticket_id, state, booking_date, user_email, tour_id " +
                    "FROM tickets WHERE user_email = ?";

    public static final String UPDATE_TICKET_STATE_BY_ID =
            "UPDATE tickets SET state = ? WHERE ticket_id = ?";

    public static final String CHECK_TICKET_EXISTS =
            "SELECT ticket_id FROM tickets WHERE ticket_id = ?";

    public static final String SELECT_ALL_TICKETS =
            "SELECT ticket_id, state, booking_date, user_email, tour_id FROM tickets";

    public static final String SELECT_TICKETS_BY_STATE =
            "SELECT ticket_id, state, booking_date, user_email, tour_id " +
                    "FROM tickets WHERE state = ?";

    // =========================
    // LOGIN
    // =========================

    public static final String FIND_USER_LOGIN =
            "SELECT user_id, name, surname, user_email " +
                    "FROM users " +
                    "WHERE user_email = ? AND password = ?";

    public static final String FIND_GUIDE_LOGIN =
            "SELECT guide_id, name, surname, guide_email " +
                    "FROM tourist_guides " +
                    "WHERE guide_email = ? AND password = ?";

    // =========================
    // GUIDES
    // =========================

    public static final String FIND_GUIDE_BY_EMAIL =
            "SELECT guide_id, name, surname, guide_email " +
                    "FROM tourist_guides " +
                    "WHERE guide_email = ?";
}
