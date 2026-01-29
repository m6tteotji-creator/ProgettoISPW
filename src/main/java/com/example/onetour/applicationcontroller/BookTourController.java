package com.example.onetour.applicationcontroller;

import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.EmailBean;
import com.example.onetour.bean.SearchBean;
import com.example.onetour.bean.TourBean;
import com.example.onetour.boundary.EmailNotificationBoundary;
import com.example.onetour.dao.TicketDAO;
import com.example.onetour.dao.TicketDAOFactorySingleton;
import com.example.onetour.dao.TourDAO;
import com.example.onetour.dao.TourDAOCatalog;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.enumeration.TicketState;
import com.example.onetour.exception.DuplicateTicketException;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.exception.TicketNotFoundException;
import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Ticket;
import com.example.onetour.model.Tour;
import com.example.onetour.model.UserAccount;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class BookTourController {

    private final TourDAO tourDAO;

    public BookTourController() {
        this.tourDAO = new TourDAOCatalog();
    }

    public List<TourBean> searchTours(SearchBean searchBean)
            throws InvalidFormatException, TourNotFoundException {

        if (searchBean == null) throw new InvalidFormatException("Invalid search");
        searchBean.checkFields();

        String sessionID = searchBean.getSessionID();
        checkSession(sessionID);

        List<Tour> tours = tourDAO.findTours(
                searchBean.getCityName(),
                searchBean.getDepartureDate(),
                searchBean.getReturnDate()
        );

        SessionManagerSingleton.getInstance()
                .getSession(sessionID)
                .setLastTourList(tours);

        List<TourBean> out = new ArrayList<>();
        for (Tour t : tours) {
            TourBean b = TourBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    public TourBean getTourDescription(TourBean tourBeanIn)
            throws InvalidFormatException, TourNotFoundException {

        if (tourBeanIn == null) throw new InvalidFormatException("Invalid request");

        String sessionID = tourBeanIn.getSessionID();
        checkSession(sessionID);

        if (tourBeanIn.getTourID() == null || tourBeanIn.getTourID().isBlank()) {
            throw new InvalidFormatException("Invalid tour");
        }

        Tour tour = tourDAO.retrieveTourFromId(tourBeanIn.getTourID());

        SessionManagerSingleton.getInstance()
                .getSession(sessionID)
                .setActualTour(tour);

        TourBean out = TourBean.fromModel(tour);
        out.setSessionID(sessionID);
        return out;
    }

    public BookingBean createTicket(BookingBean bookingBean)
            throws InvalidFormatException, DuplicateTicketException {

        if (bookingBean == null) throw new InvalidFormatException("Invalid booking");

        String sessionID = bookingBean.getSessionID();
        checkSession(sessionID);

        UserAccount user = getUserFromSession(sessionID);
        validateNotGuide(user);

        String userEmail = validateUserEmail(user);
        String tourId = resolveTourId(sessionID, bookingBean);

        Tour selectedTour = loadTourById(tourId);
        SessionManagerSingleton.getInstance()
                .getSession(sessionID)
                .setActualTour(selectedTour);

        TicketDAO ticketDAO = buildTicketDAO();
        enforceNoActiveDuplicate(ticketDAO, userEmail, tourId);

        Ticket ticket = new Ticket(userEmail, selectedTour);
        ticketDAO.create(ticket);

        BookingBean out = BookingBean.fromModel(ticket);
        out.setSessionID(sessionID);
        return out;
    }

    public List<BookingBean> getMyBookings(String sessionID)
            throws InvalidFormatException, TicketNotFoundException {

        checkSession(sessionID);

        UserAccount user = getUserFromSession(sessionID);
        String email = validateUserEmail(user);

        TicketDAO ticketDAO = buildTicketDAO();
        List<Ticket> ticketList = ticketDAO.retrieveByUser(email);

        List<BookingBean> out = new ArrayList<>();
        for (Ticket t : ticketList) {
            BookingBean b = BookingBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    public List<BookingBean> getPendingRequests(String sessionID)
            throws InvalidFormatException, TicketNotFoundException {

        checkSession(sessionID);

        UserAccount actor = getUserFromSession(sessionID);
        validateIsGuide(actor);

        String guideEmail = validateUserEmail(actor);

        TicketDAO ticketDAO = buildTicketDAO();
        List<Ticket> pendingTickets = ticketDAO.retrievePendingByGuide(guideEmail);

        List<BookingBean> out = new ArrayList<>();
        for (Ticket t : pendingTickets) {
            BookingBean b = BookingBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    public List<BookingBean> getAllRequestsByGuide(String sessionID)
            throws InvalidFormatException, TicketNotFoundException {

        checkSession(sessionID);

        UserAccount actor = getUserFromSession(sessionID);
        validateIsGuide(actor);

        String guideEmail = validateUserEmail(actor);

        TicketDAO ticketDAO = buildTicketDAO();
        List<Ticket> allTickets = ticketDAO.retrieveByGuide(guideEmail);

        List<BookingBean> out = new ArrayList<>();
        for (Ticket t : allTickets) {
            BookingBean b = BookingBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    public void guideDecision(EmailBean emailBean)
            throws InvalidFormatException, TicketNotFoundException {

        if (emailBean == null) throw new InvalidFormatException("Invalid decision");

        String sessionID = emailBean.getSessionID();
        checkSession(sessionID);

        UserAccount actor = getUserFromSession(sessionID);
        validateIsGuide(actor);

        validateDecisionBean(emailBean);

        TicketDAO ticketDAO = buildTicketDAO();

        List<Ticket> allTickets = ticketDAO.retrieveByGuide(actor.getUserEmail());
        Ticket ticket = allTickets.stream()
                .filter(t -> emailBean.getTicketID().equals(t.getTicketID()))
                .findFirst()
                .orElseThrow(() ->
                        new TicketNotFoundException("Ticket not found: " + emailBean.getTicketID()));

        ticketDAO.modifyState(ticket.getTicketID(), emailBean.getDecision());

        emailBean.setGuideEmail(actor.getUserEmail());
        emailBean.setUserEmail(ticket.getUserEmail());
        emailBean.setTourID(ticket.getTour().getTourID());

        new EmailNotificationBoundary().sendNotification(emailBean);
    }

    /* =======================
       Utility methods
       ======================= */

    private static UserAccount getUserFromSession(String sessionID) throws InvalidFormatException {
        UserAccount user = SessionManagerSingleton.getInstance()
                .getSession(sessionID)
                .getUser();
        if (user == null) throw new InvalidFormatException("User not in session");
        return user;
    }

    private static void validateNotGuide(UserAccount user) throws InvalidFormatException {
        if (user.getRole() == RoleEnum.TOURISTGUIDE) {
            throw new InvalidFormatException("Operation not allowed: guides cannot book tours");
        }
    }

    private static void validateIsGuide(UserAccount user) throws InvalidFormatException {
        if (user.getRole() != RoleEnum.TOURISTGUIDE) {
            throw new InvalidFormatException("Operation not allowed: only tourist guides can view requests");
        }
    }

    private static String validateUserEmail(UserAccount user) throws InvalidFormatException {
        String email = user.getUserEmail();
        if (email == null || email.isBlank()) throw new InvalidFormatException("Invalid user email");
        return email;
    }

    private static String resolveTourId(String sessionID, BookingBean bookingBean)
            throws InvalidFormatException {

        String tourId = bookingBean.getTourID();
        if (tourId != null && !tourId.isBlank()) return tourId;

        Tour fallback = SessionManagerSingleton.getInstance()
                .getSession(sessionID)
                .getActualTour();

        if (fallback == null || fallback.getTourID() == null || fallback.getTourID().isBlank()) {
            throw new InvalidFormatException("No selected tour");
        }
        return fallback.getTourID();
    }

    private Tour loadTourById(String tourId) throws InvalidFormatException {
        try {
            return tourDAO.retrieveTourFromId(tourId);
        } catch (TourNotFoundException e) {
            throw new InvalidFormatException("Tour not found", e);
        }
    }

    private static void enforceNoActiveDuplicate(TicketDAO ticketDAO, String userEmail, String tourId)
            throws DuplicateTicketException {

        List<Ticket> existing;
        try {
            existing = ticketDAO.retrieveByUser(userEmail);
        } catch (TicketNotFoundException ignored) {
            return;
        }

        for (Ticket t : existing) {
            String existingTourId = (t != null && t.getTour() != null) ? t.getTour().getTourID() : null;
            if (existingTourId == null) {
                continue; // single continue (allowed by the rule)
            }

            boolean sameTour = existingTourId.equals(tourId);
            TicketState st = t.getState();
            boolean active = (st == TicketState.PENDING || st == TicketState.CONFIRMED);

            if (sameTour && active) {
                throw new DuplicateTicketException(
                        "You already have an active booking request for this tour (state=" + st + ")."
                );
            }
        }
    }

    private static void validateDecisionBean(EmailBean emailBean) throws InvalidFormatException {
        if (emailBean.getTicketID() == null || emailBean.getTicketID().isBlank()) {
            throw new InvalidFormatException("Missing ticket id");
        }
        if (emailBean.getDecision() == null) {
            throw new InvalidFormatException("Invalid decision");
        }
    }

    private static TicketDAO buildTicketDAO() throws InvalidFormatException {
        try {
            return TicketDAOFactorySingleton.getInstance().createTicketDAO();
        } catch (Exception e) {
            throw new InvalidFormatException("Persistence configuration error (ticket DAO)", e);
        }
    }

    private static void checkSession(String sessionID) throws InvalidFormatException {
        if (sessionID == null || sessionID.isBlank())
            throw new InvalidFormatException("Session missing");
        if (SessionManagerSingleton.getInstance().getSession(sessionID) == null)
            throw new InvalidFormatException("Invalid session");
    }
}
