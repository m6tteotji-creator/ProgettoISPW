package com.example.onetour.dao.db;

public final class Queries {

    private Queries() {}

    // =========================
    // TICKETS
    // =========================
    public static final String INSERT_TICKET =
            "INSERT INTO tickets (ticket_id, state, booking_date, user_email, tour_id) VALUES (?, ?, ?, ?, ?)";

    public static final String SELECT_TICKETS_BY_USER =
            "SELECT ticket_id, state, booking_date, user_email, tour_id " +
                    "FROM tickets WHERE user_email = ?";

    public static final String UPDATE_TICKET_STATE_BY_ID =
            "UPDATE tickets SET state = ? WHERE ticket_id = ?";

    public static final String CHECK_TICKET_EXISTS =
            "SELECT ticket_id FROM tickets WHERE ticket_id = ?";

    public static final String SELECT_PENDING_TICKETS_BY_GUIDE_EMAIL =
            "SELECT t.ticket_id, t.state, t.booking_date, t.user_email, t.tour_id " +
                    "FROM tickets t " +
                    "JOIN tours tr ON tr.tour_id = t.tour_id " +
                    "WHERE tr.guide_email = ? AND t.state = ?";

    public static final String SELECT_TICKETS_BY_GUIDE_EMAIL =
            "SELECT t.ticket_id, t.state, t.booking_date, t.user_email, t.tour_id " +
                    "FROM tickets t " +
                    "JOIN tours tr ON tr.tour_id = t.tour_id " +
                    "WHERE tr.guide_email = ?";

    // =========================
    // TOURS
    // =========================
    public static final String SELECT_TOUR_BY_ID =
            "SELECT tour_id, name_tour, city_name, departure_date, return_date, price, guide_email " +
                    "FROM tours WHERE tour_id = ?";

    public static final String SELECT_TOURS_BY_CITY_AND_DATES =
            "SELECT tour_id, name_tour, city_name, departure_date, return_date, price, guide_email " +
                    "FROM tours WHERE city_name = ? AND departure_date >= ? AND return_date <= ?";

    public static final String SELECT_TOUR_BY_NAME_AND_DATES =
            "SELECT tour_id, name_tour, city_name, departure_date, return_date, price, guide_email " +
                    "FROM tours WHERE name_tour = ? AND departure_date = ? AND return_date = ?";

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
    // GUIDES (NEW)
    // =========================
    public static final String FIND_GUIDE_BY_EMAIL =
            "SELECT guide_id, name, surname, guide_email " +
                    "FROM tourist_guides " +
                    "WHERE guide_email = ?";
}
