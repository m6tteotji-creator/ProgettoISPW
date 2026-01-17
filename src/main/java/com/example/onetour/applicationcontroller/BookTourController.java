package com.example.onetour.applicationcontroller;

import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.EmailBean;
import com.example.onetour.bean.SearchBean;
import com.example.onetour.bean.TourBean;
import com.example.onetour.boundary.EmailNotificationBoundary;
import com.example.onetour.config.AppConfig;
import com.example.onetour.dao.TicketDAO;
import com.example.onetour.dao.TicketDAOFactorySingleton;
import com.example.onetour.dao.TourDAO;
import com.example.onetour.dao.TourDAOMemory;
import com.example.onetour.dao.TourDAOJDBC;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.exception.DuplicateTicketException;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.model.Ticket;
import com.example.onetour.model.Tour;
import com.example.onetour.model.UserAccount;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;

import java.util.ArrayList;
import java.util.List;

public class BookTourController {

    public List<TourBean> searchTours(SearchBean searchBean) throws Exception {
        if (searchBean == null) throw new InvalidFormatException("Invalid search");
        searchBean.checkFields();

        String sessionID = searchBean.getSessionID();
        checkSession(sessionID);

        TourDAO tourDAO = buildTourDAO();
        List<Tour> tours = tourDAO.findTours(
                searchBean.getCityName(),
                searchBean.getDepartureDate(),
                searchBean.getReturnDate()
        );

        SessionManagerSingleton.getInstance().getSession(sessionID).setLastTourList(tours);

        List<TourBean> out = new ArrayList<>();
        for (Tour t : tours) {
            TourBean b = TourBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    public TourBean getTourDescription(TourBean tourBeanIn) throws Exception {
        if (tourBeanIn == null) throw new InvalidFormatException("Invalid request");

        String sessionID = tourBeanIn.getSessionID();
        checkSession(sessionID);

        if (tourBeanIn.getTourID() == null || tourBeanIn.getTourID().isBlank()) {
            throw new InvalidFormatException("Invalid tour");
        }

        TourDAO tourDAO = buildTourDAO();
        Tour tour = tourDAO.retrieveTourFromId(tourBeanIn.getTourID());
        if (tour == null) throw new InvalidFormatException("Tour not found");

        SessionManagerSingleton.getInstance().getSession(sessionID).setActualTour(tour);

        TourBean out = TourBean.fromModel(tour);
        out.setSessionID(sessionID);
        return out;
    }

    public BookingBean createTicket(BookingBean bookingBean) throws Exception {
        if (bookingBean == null) throw new InvalidFormatException("Invalid booking");

        String sessionID = bookingBean.getSessionID();
        checkSession(sessionID);

        UserAccount user = SessionManagerSingleton.getInstance().getSession(sessionID).getUser();
        if (user == null) throw new InvalidFormatException("User not in session");

        if (user.getRole() == RoleEnum.TOURISTGUIDE) {
            throw new InvalidFormatException("Operation not allowed: guides cannot book tours");
        }

        String userEmail = user.getUserEmail();
        if (userEmail == null || userEmail.isBlank()) throw new InvalidFormatException("Invalid user email");

        Tour selectedTour = SessionManagerSingleton.getInstance().getSession(sessionID).getActualTour();
        if (selectedTour == null) throw new InvalidFormatException("No selected tour");

        Ticket ticket = new Ticket(userEmail, selectedTour);

        TicketDAO ticketDAO = TicketDAOFactorySingleton.getInstance().createTicketDAO();
        try {
            ticketDAO.create(ticket);
        } catch (DuplicateTicketException e) {
            throw new InvalidFormatException(e.getMessage());
        }

        BookingBean out = BookingBean.fromModel(ticket);
        out.setSessionID(sessionID);
        return out;
    }

    public List<BookingBean> getMyBookings(String sessionID) throws Exception {
        checkSession(sessionID);

        UserAccount user = SessionManagerSingleton.getInstance().getSession(sessionID).getUser();
        if (user == null) throw new InvalidFormatException("User not in session");

        String email = user.getUserEmail();
        if (email == null || email.isBlank()) throw new InvalidFormatException("Invalid user email");

        TicketDAO ticketDAO = TicketDAOFactorySingleton.getInstance().createTicketDAO();
        List<Ticket> ticketList = ticketDAO.retrieveByUser(email);

        List<BookingBean> out = new ArrayList<>();
        for (Ticket t : ticketList) {
            BookingBean b = BookingBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    public List<BookingBean> getPendingRequests(String sessionID) throws Exception {
        checkSession(sessionID);

        UserAccount actor = SessionManagerSingleton.getInstance().getSession(sessionID).getUser();
        if (actor == null) throw new InvalidFormatException("User not in session");

        if (actor.getRole() != RoleEnum.TOURISTGUIDE) {
            throw new InvalidFormatException("Operation not allowed: only tourist guides can view pending requests");
        }

        String guideEmail = actor.getUserEmail();
        if (guideEmail == null || guideEmail.isBlank()) {
            throw new InvalidFormatException("Invalid guide email");
        }

        TicketDAO ticketDAO = TicketDAOFactorySingleton.getInstance().createTicketDAO();
        List<Ticket> pendingTickets = ticketDAO.retrievePendingByGuide(guideEmail);

        List<BookingBean> out = new ArrayList<>();
        for (Ticket t : pendingTickets) {
            BookingBean b = BookingBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    // ✅ NUOVO: Pag.9 deve vedere pending + vecchie richieste
    public List<BookingBean> getAllRequestsByGuide(String sessionID) throws Exception {
        checkSession(sessionID);

        UserAccount actor = SessionManagerSingleton.getInstance().getSession(sessionID).getUser();
        if (actor == null) throw new InvalidFormatException("User not in session");

        if (actor.getRole() != RoleEnum.TOURISTGUIDE) {
            throw new InvalidFormatException("Operation not allowed: only tourist guides can view requests");
        }

        String guideEmail = actor.getUserEmail();
        if (guideEmail == null || guideEmail.isBlank()) {
            throw new InvalidFormatException("Invalid guide email");
        }

        TicketDAO ticketDAO = TicketDAOFactorySingleton.getInstance().createTicketDAO();
        List<Ticket> allTickets = ticketDAO.retrieveByGuide(guideEmail);

        List<BookingBean> out = new ArrayList<>();
        for (Ticket t : allTickets) {
            BookingBean b = BookingBean.fromModel(t);
            b.setSessionID(sessionID);
            out.add(b);
        }
        return out;
    }

    public void guideDecision(EmailBean emailBean) throws Exception {
        if (emailBean == null) throw new InvalidFormatException("Invalid decision");

        String sessionID = emailBean.getSessionID();
        checkSession(sessionID);

        UserAccount actor = SessionManagerSingleton.getInstance().getSession(sessionID).getUser();
        if (actor == null) throw new InvalidFormatException("User not in session");
        if (actor.getRole() != RoleEnum.TOURISTGUIDE) {
            throw new InvalidFormatException("Operation not allowed: only tourist guides can decide");
        }

        if (emailBean.getTicketID() == null || emailBean.getTicketID().isBlank()) {
            throw new InvalidFormatException("Missing ticket id");
        }
        if (emailBean.getDecision() == null) {
            throw new InvalidFormatException("Invalid decision");
        }

        TicketDAO ticketDAO = TicketDAOFactorySingleton.getInstance().createTicketDAO();
        ticketDAO.modifyState(emailBean.getTicketID(), emailBean.getDecision());

        EmailNotificationBoundary boundary = new EmailNotificationBoundary();
        boundary.sendNotification(emailBean);
    }

    private TourDAO buildTourDAO() {
        String persistence = AppConfig.getInstance().get("tour.persistence", "MEMORY").trim().toUpperCase();

        return switch (persistence) {
            case "MYSQL", "JDBC" -> new TourDAOJDBC();
            case "MEMORY", "DEMO" -> new TourDAOMemory();
            default -> throw new IllegalStateException("Unsupported tour.persistence: " + persistence);
        };
    }

    private static void checkSession(String sessionID) throws InvalidFormatException {
        if (sessionID == null || sessionID.isBlank())
            throw new InvalidFormatException("Session missing");
        if (SessionManagerSingleton.getInstance().getSession(sessionID) == null)
            throw new InvalidFormatException("Invalid session");
    }
}
