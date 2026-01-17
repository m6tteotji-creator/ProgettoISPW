package com.example.onetour;

import com.example.onetour.applicationController.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.EmailBean;
import com.example.onetour.bean.SearchBean;
import com.example.onetour.bean.TourBean;
import com.example.onetour.dao.TicketDAO;
import com.example.onetour.dao.TicketDAOFactorySingleton;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.enumeration.TicketState;
import com.example.onetour.model.Ticket;
import com.example.onetour.model.Tour;
import com.example.onetour.model.TouristGuide;
import com.example.onetour.model.UserAccount;
import com.example.onetour.sessionManagement.SessionManagerSingleton;

import java.time.LocalDate;
import java.util.List;

public class MainTest {

    public static void main(String[] args) throws Exception {

        // =========================
        // 1) SESSIONE USER
        // =========================
        UserAccount user = new UserAccount("U_TEST", "User Test", "user@test.com", RoleEnum.USER);
        String userSessionID = SessionManagerSingleton.getInstance().addSession(user);

        BookTourController controller = new BookTourController();
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to   = LocalDate.now().plusYears(1);

        SearchBean sb = new SearchBean();
        sb.setSessionID(userSessionID);
        sb.setCityName("Roma");
        sb.setDepartureDate(from);
        sb.setReturnDate(to);

        List<TourBean> found = controller.searchTours(sb);
        System.out.println("TOURS FOUND: " + found.size());
        if (found.isEmpty()) {
            System.out.println("No tours found. (Se sei in DEMO, controlla che TourDAOMemory abbia un seed)");
            return;
        }

        String tourID = found.get(0).getTourID();
        System.out.println("FIRST TOUR ID: " + tourID);

        TourBean in = new TourBean();
        in.setSessionID(userSessionID);
        in.setTourID(tourID);
        controller.getTourDescription(in);

        Tour selectedTour = SessionManagerSingleton.getInstance().getSession(userSessionID).getActualTour();
        if (selectedTour == null) {
            throw new IllegalStateException("actualTour is null in session. getTourDescription() non ha funzionato.");
        }
        TouristGuide guide = selectedTour.getTouristGuide();
        if (guide == null || guide.getEmail() == null || guide.getEmail().isBlank()) {
            throw new IllegalStateException("TouristGuide/email missing nel Tour. Impossibile testare retrievePendingByGuide.");
        }
        String guideEmail = guide.getEmail();
        System.out.println("GUIDE EMAIL (from selected tour): " + guideEmail);

        // =========================
        // 2) CREATE TICKET (USER)
        // =========================
        BookingBean bb = new BookingBean();
        bb.setSessionID(userSessionID);

        BookingBean created = controller.createTicket(bb);
        String ticketID = created.getTicketID();
        System.out.println("CREATED TICKET ID: " + ticketID + " (expected state=PENDING)");

        // =========================
        // 3) SESSIONE GUIDA
        // =========================
        UserAccount guideAccount = new UserAccount("G_TEST", "Guide Test", guideEmail, RoleEnum.TOURISTGUIDE);
        String guideSessionID = SessionManagerSingleton.getInstance().addSession(guideAccount);

        // =========================
        // 4) RETRIEVE PENDING BY GUIDE (DAO)
        // =========================
        TicketDAO ticketDAO = TicketDAOFactorySingleton.getInstance().createTicketDAO();
        List<Ticket> pending = ticketDAO.retrievePendingByGuide(guideEmail);

        System.out.println("PENDING FOR GUIDE: " + pending.size());
        for (Ticket t : pending) {
            System.out.println(" - pending ticket: " + t.getTicketID()
                    + " user=" + t.getUserEmail()
                    + " tour=" + (t.getTour() != null ? t.getTour().getTourID() : "null"));
        }

        // =========================
        // 5) GUIDA CONFERMA (guideDecision)
        // =========================
        EmailBean eb = new EmailBean();
        eb.setSessionID(guideSessionID);   // IMPORTANT: sessione GUIDA, non sessione USER
        eb.setTicketID(ticketID);
        eb.setDecision(TicketState.CONFIRMED);

        controller.guideDecision(eb);
        System.out.println("STATE UPDATED TO CONFIRMED");

        // =========================
        // 6) CHECK: USER vede lo stato aggiornato
        // =========================
        List<BookingBean> my = controller.getMyBookings(userSessionID);
        System.out.println("MY BOOKINGS: " + my.size());
        for (BookingBean b : my) {
            System.out.println(" - booking: " + b.getTicketID() + " state=" + b.getState());
        }
    }
}
