package com.example.onetour.dao;

import com.example.onetour.enumeration.TicketState;
import com.example.onetour.exception.DuplicateTicketException;
import com.example.onetour.exception.TicketNotFoundException;
import com.example.onetour.model.Ticket;
import com.example.onetour.model.Tour;
import com.example.onetour.model.TouristGuide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDAOMemory extends TicketDAO {

    private static final Map<String, Ticket> STORE = new HashMap<>();

    @Override
    public synchronized void create(Ticket ticket) throws DuplicateTicketException {
        if (ticket == null) throw new IllegalArgumentException("Ticket is null");
        if (ticket.getTicketID() == null || ticket.getTicketID().isBlank())
            throw new IllegalArgumentException("ticketID missing");

        if (STORE.containsKey(ticket.getTicketID())) {
            throw new DuplicateTicketException("Duplicate ticketID: " + ticket.getTicketID());
        }

        STORE.put(ticket.getTicketID(), ticket);
    }

    @Override
    public synchronized List<Ticket> retrieveByUser(String userEmail) throws TicketNotFoundException {
        if (userEmail == null || userEmail.isBlank())
            throw new IllegalArgumentException("userEmail is null/blank");

        List<Ticket> out = new ArrayList<>();
        for (Ticket t : STORE.values()) {
            if (t.getUserEmail() != null && t.getUserEmail().equalsIgnoreCase(userEmail)) {
                out.add(t);
            }
        }

        if (out.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for user: " + userEmail);
        }
        return out;
    }

    @Override
    public synchronized List<Ticket> retrievePendingByGuide(String guideEmail) throws TicketNotFoundException {
        if (guideEmail == null || guideEmail.isBlank())
            throw new IllegalArgumentException("guideEmail is null/blank");

        List<Ticket> out = new ArrayList<>();

        for (Ticket t : STORE.values()) {
            if (t == null) continue;
            if (t.getState() != TicketState.PENDING) continue;

            Tour tour = t.getTour();
            if (tour == null) continue;

            TouristGuide g = tour.getTouristGuide();
            if (g == null) continue;

            String email = g.getEmail();
            if (email != null && email.equalsIgnoreCase(guideEmail)) {
                out.add(t);
            }
        }

        if (out.isEmpty()) {
            throw new TicketNotFoundException("No pending tickets found for guide: " + guideEmail);
        }
        return out;
    }

    // ✅ NUOVO: tutte le richieste della guida (pending + vecchie)
    @Override
    public synchronized List<Ticket> retrieveByGuide(String guideEmail) throws TicketNotFoundException {
        if (guideEmail == null || guideEmail.isBlank())
            throw new IllegalArgumentException("guideEmail is null/blank");

        List<Ticket> out = new ArrayList<>();

        for (Ticket t : STORE.values()) {
            if (t == null) continue;

            Tour tour = t.getTour();
            if (tour == null) continue;

            TouristGuide g = tour.getTouristGuide();
            if (g == null) continue;

            String email = g.getEmail();
            if (email != null && email.equalsIgnoreCase(guideEmail)) {
                out.add(t);
            }
        }

        if (out.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for guide: " + guideEmail);
        }
        return out;
    }

    @Override
    public synchronized void modifyState(String ticketID, TicketState newState) throws TicketNotFoundException {
        if (ticketID == null || ticketID.isBlank())
            throw new IllegalArgumentException("ticketID is null/blank");
        if (newState == null)
            throw new IllegalArgumentException("newState is null");

        Ticket t = STORE.get(ticketID);
        if (t == null) throw new TicketNotFoundException("Ticket not found: " + ticketID);

        t.setState(newState);
    }

    public static synchronized void clearAll() {
        STORE.clear();
    }
}
