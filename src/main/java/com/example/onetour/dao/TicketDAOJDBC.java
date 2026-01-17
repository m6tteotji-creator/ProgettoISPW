package com.example.onetour.dao;

import com.example.onetour.dao.db.DBConnection;
import com.example.onetour.dao.db.Queries;
import com.example.onetour.enumeration.TicketState;
import com.example.onetour.exception.DuplicateTicketException;
import com.example.onetour.exception.TicketNotFoundException;
import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Ticket;
import com.example.onetour.model.Tour;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TicketDAOJDBC extends TicketDAO {

    private final TourDAO tourDAO = new TourDAOJDBC();

    @Override
    public void create(Ticket ticket) throws DuplicateTicketException {
        validateTicketForCreate(ticket);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Queries.INSERT_TICKET)) {

            ps.setString(1, ticket.getTicketID());
            ps.setString(2, ticket.getState().name());
            ps.setDate(3, Date.valueOf(ticket.getBookingDate()));
            ps.setString(4, ticket.getUserEmail());
            ps.setString(5, ticket.getTour().getTourID());

            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new DAOException("Insert failed for ticket_id=" + ticket.getTicketID());
            }

        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                throw new DuplicateTicketException(
                        "Duplicate ticket (same user and tour) or constraint violation",
                        e
                );
            }
            throw new DAOException("DB error while creating ticket", e);
        }
    }

    @Override
    public List<Ticket> retrieveByUser(String userEmail) throws TicketNotFoundException {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("userEmail missing");
        }

        List<Ticket> out = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Queries.SELECT_TICKETS_BY_USER)) {

            ps.setString(1, userEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            // FIX: Uso eccezione specifica
            throw new DAOException("DB error while retrieving tickets by user", e);
        }

        if (out.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for user " + userEmail);
        }
        return out;
    }

    @Override
    public List<Ticket> retrievePendingByGuide(String guideEmail) throws TicketNotFoundException {
        if (guideEmail == null || guideEmail.isBlank()) {
            throw new IllegalArgumentException("guideEmail missing");
        }

        List<Ticket> out = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Queries.SELECT_PENDING_TICKETS_BY_GUIDE_EMAIL)) {

            ps.setString(1, guideEmail);
            ps.setString(2, TicketState.PENDING.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("DB error while retrieving pending tickets by guide", e);
        }

        if (out.isEmpty()) {
            throw new TicketNotFoundException("No pending tickets for guide " + guideEmail);
        }
        return out;
    }

    @Override
    public List<Ticket> retrieveByGuide(String guideEmail) throws TicketNotFoundException {
        if (guideEmail == null || guideEmail.isBlank()) {
            throw new IllegalArgumentException("guideEmail missing");
        }

        List<Ticket> out = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Queries.SELECT_TICKETS_BY_GUIDE_EMAIL)) {

            ps.setString(1, guideEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            // FIX: Uso eccezione specifica
            throw new DAOException("DB error while retrieving tickets by guide", e);
        }

        if (out.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for guide " + guideEmail);
        }
        return out;
    }

    @Override
    public void modifyState(String ticketID, TicketState newState) throws TicketNotFoundException {
        if (ticketID == null || ticketID.isBlank()) {
            throw new IllegalArgumentException("ticketID missing");
        }
        if (newState == null) {
            throw new IllegalArgumentException("newState missing");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(Queries.UPDATE_TICKET_STATE_BY_ID)) {

            ps.setString(1, newState.name());
            ps.setString(2, ticketID);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new TicketNotFoundException("Ticket not found: " + ticketID);
            }

        } catch (SQLException e) {
            throw new DAOException("DB error while updating ticket state", e);
        }
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        String ticketID = rs.getString("ticket_id");
        LocalDate bookingDate = rs.getDate("booking_date").toLocalDate();
        TicketState state = TicketState.valueOf(rs.getString("state"));
        String userEmail = rs.getString("user_email");
        String tourID = rs.getString("tour_id");

        Ticket t = new Ticket(ticketID, bookingDate, state, userEmail);

        try {
            Tour fullTour = tourDAO.retrieveTourFromId(tourID);
            t.setTour(fullTour);
        } catch (TourNotFoundException e) {
            Tour minimal = new Tour(tourID, "", "", bookingDate, bookingDate, 0.0);
            t.setTour(minimal);
        }

        return t;
    }

    private void validateTicketForCreate(Ticket ticket) {
        if (ticket == null) throw new IllegalArgumentException("Ticket is null");
        if (ticket.getTour() == null) throw new IllegalArgumentException("Ticket tour is null");
        if (ticket.getTicketID() == null || ticket.getTicketID().isBlank()) throw new IllegalArgumentException("ticket_id missing");
        if (ticket.getUserEmail() == null || ticket.getUserEmail().isBlank()) throw new IllegalArgumentException("user_email missing");
        if (ticket.getTour().getTourID() == null || ticket.getTour().getTourID().isBlank()) throw new IllegalArgumentException("tour_id missing");
        if (ticket.getBookingDate() == null) throw new IllegalArgumentException("booking_date missing");
        if (ticket.getState() == null) throw new IllegalArgumentException("state missing");
    }

    public static class DAOException extends RuntimeException {
        public DAOException(String message) {
            super(message);
        }
        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}