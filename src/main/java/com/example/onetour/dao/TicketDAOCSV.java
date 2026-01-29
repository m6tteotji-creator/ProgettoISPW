package com.example.onetour.dao;

import com.example.onetour.enumeration.TicketState;
import com.example.onetour.exception.DuplicateTicketException;
import com.example.onetour.exception.TicketNotFoundException;
import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Ticket;
import com.example.onetour.model.Tour;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketDAOCSV extends TicketDAO {

    // 0 ticketID | 1 state | 2 bookingDate | 3 userEmail | 4 tourID
    private static final int IDX_TICKET_ID = 0;
    private static final int IDX_STATE = 1;
    private static final int IDX_BOOKING_DATE = 2;
    private static final int IDX_USER_EMAIL = 3;
    private static final int IDX_TOUR_ID = 4;

    private static final Logger logger = Logger.getLogger(TicketDAOCSV.class.getName());

    private static final Path CSV_PATH =
            Path.of(System.getProperty("user.home"), "onetour-data", "tickets.csv");

    private final File fd;

    // Fixed source of tours: MEMORY only
    private final TourDAO tourDAO = new TourDAOCatalog();

    public TicketDAOCSV() {
        try {
            Files.createDirectories(CSV_PATH.getParent());
            this.fd = CSV_PATH.toFile();

            if (!fd.exists()) {
                boolean created = fd.createNewFile();
                if (!created) {
                    throw new IOException("Failed to create CSV file: " + fd.getAbsolutePath());
                }
            }

        } catch (IOException e) {
            throw new CSVPersistenceException("Cannot initialize TicketDAOCSV: " + CSV_PATH, e);
        }
    }

    @Override
    public synchronized void create(Ticket ticket) throws DuplicateTicketException {
        validateTicketForCreate(ticket);

        try {
            if (existsById(ticket.getTicketID())) {
                throw new DuplicateTicketException("Duplicate ticketID: " + ticket.getTicketID());
            }
            append(ticket);
        } catch (IOException | CsvValidationException e) {
            throw new CSVPersistenceException("CSV persistence error (create)", e);
        }
    }

    @Override
    public synchronized List<Ticket> retrieveByUser(String userEmail) throws TicketNotFoundException {
        if (userEmail == null || userEmail.isBlank()) {
            throw new TicketNotFoundException("Invalid userEmail");
        }

        try {
            List<Ticket> res = readFiltered(row -> sameUserEmail(row, userEmail));

            if (res.isEmpty()) {
                throw new TicketNotFoundException("No tickets for user: " + userEmail);
            }
            return res;

        } catch (IOException | CsvValidationException e) {
            throw new CSVPersistenceException("CSV persistence error (retrieveByUser)", e);
        }
    }

    @Override
    public synchronized List<Ticket> retrievePendingByGuide(String guideEmail) throws TicketNotFoundException {
        if (guideEmail == null || guideEmail.isBlank()) {
            throw new IllegalArgumentException("guideEmail is null/blank");
        }

        try {
            List<Ticket> res = readFiltered(row -> {
                String st = row[IDX_STATE];
                if (!TicketState.PENDING.name().equals(st)) {
                    return false;
                }
                return isRowMatchingGuide(row, guideEmail);
            });

            if (res.isEmpty()) {
                throw new TicketNotFoundException("No pending tickets for guide: " + guideEmail);
            }
            return res;

        } catch (IOException | CsvValidationException e) {
            throw new CSVPersistenceException("CSV persistence error (retrievePendingByGuide)", e);
        }
    }

    @Override
    public synchronized List<Ticket> retrieveByGuide(String guideEmail) throws TicketNotFoundException {
        if (guideEmail == null || guideEmail.isBlank()) {
            throw new IllegalArgumentException("guideEmail is null/blank");
        }

        try {
            List<Ticket> res = readFiltered(row -> isRowMatchingGuide(row, guideEmail));

            if (res.isEmpty()) {
                throw new TicketNotFoundException("No tickets for guide: " + guideEmail);
            }
            return res;

        } catch (IOException | CsvValidationException e) {
            throw new CSVPersistenceException("CSV persistence error (retrieveByGuide)", e);
        }
    }

    @Override
    public synchronized void modifyState(String ticketID, TicketState newState) throws TicketNotFoundException {
        if (ticketID == null || ticketID.isBlank()) {
            throw new TicketNotFoundException("Missing ticketID");
        }
        if (newState == null) {
            throw new TicketNotFoundException("Missing newState");
        }

        try {
            List<String[]> rows = readAllRows();
            boolean updated = false;

            for (String[] row : rows) {
                if (sameTicketId(row, ticketID)) {
                    row[IDX_STATE] = newState.name();
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                throw new TicketNotFoundException("Ticket not found: " + ticketID);
            }

            rewriteAll(rows);

        } catch (IOException | CsvValidationException e) {
            throw new CSVPersistenceException("CSV persistence error (modifyState)", e);
        }
    }

    private boolean isRowMatchingGuide(String[] row, String guideEmail) {
        String tourId = row[IDX_TOUR_ID];
        if (tourId == null || tourId.isBlank()) return false;

        try {
            Tour tour = tourDAO.retrieveTourFromId(tourId);
            if (tour.getTouristGuide() == null || tour.getTouristGuide().getEmail() == null) return false;
            return tour.getTouristGuide().getEmail().equalsIgnoreCase(guideEmail);

        } catch (TourNotFoundException e) {
            logger.log(Level.WARNING, e,
                    () -> "Skipping row: cannot rebuild Tour for tour_id=" + tourId);
            return false;
        }
    }

    private void validateTicketForCreate(Ticket ticket) {
        if (ticket == null) throw new IllegalArgumentException("Ticket is null");
        if (ticket.getTicketID() == null || ticket.getTicketID().isBlank())
            throw new IllegalArgumentException("ticketID missing");
        if (ticket.getUserEmail() == null || ticket.getUserEmail().isBlank())
            throw new IllegalArgumentException("userEmail missing");
        if (ticket.getBookingDate() == null)
            throw new IllegalArgumentException("bookingDate missing");
        if (ticket.getState() == null)
            throw new IllegalArgumentException("state missing");
        if (ticket.getTour() == null
                || ticket.getTour().getTourID() == null
                || ticket.getTour().getTourID().isBlank())
            throw new IllegalArgumentException("tourID missing");
    }

    private boolean existsById(String ticketID) throws IOException, CsvValidationException {
        try (CSVReader r = new CSVReader(new BufferedReader(new FileReader(fd)))) {
            String[] row;
            while ((row = r.readNext()) != null) {
                if (sameTicketId(row, ticketID)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void append(Ticket ticket) throws IOException {
        String[] rec = new String[5];
        rec[IDX_TICKET_ID] = ticket.getTicketID();
        rec[IDX_STATE] = ticket.getState().name();
        rec[IDX_BOOKING_DATE] = String.valueOf(ticket.getBookingDate());
        rec[IDX_USER_EMAIL] = ticket.getUserEmail();
        rec[IDX_TOUR_ID] = ticket.getTour().getTourID();

        try (CSVWriter w = new CSVWriter(new BufferedWriter(new FileWriter(fd, true)))) {
            w.writeNext(rec);
            w.flush();
        }
    }

    private List<Ticket> readFiltered(RowPredicate predicate)
            throws IOException, CsvValidationException {

        List<Ticket> out = new ArrayList<>();

        try (CSVReader r = new CSVReader(new BufferedReader(new FileReader(fd)))) {
            String[] row;

            while ((row = r.readNext()) != null) {
                if (!isRowEligible(row, predicate)) {
                    continue;
                }

                try {
                    out.add(mapRowToTicket(row));
                } catch (CSVPersistenceException ex) {
                    logger.log(Level.WARNING, ex::getMessage);
                }
            }
        }

        return out;
    }

    private boolean isRowEligible(String[] row, RowPredicate predicate) {
        return row != null
                && row.length >= 5
                && predicate != null
                && predicate.test(row);
    }

    private List<String[]> readAllRows() throws IOException, CsvValidationException {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader r = new CSVReader(new BufferedReader(new FileReader(fd)))) {
            String[] row;
            while ((row = r.readNext()) != null) {
                if (row.length >= 5) {
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private void rewriteAll(List<String[]> rows) throws IOException {
        try (CSVWriter w = new CSVWriter(new BufferedWriter(new FileWriter(fd, false)))) {
            w.writeAll(rows);
            w.flush();
        }
    }

    private Ticket mapRowToTicket(String[] row) {
        String id = row[IDX_TICKET_ID];
        TicketState st = TicketState.valueOf(row[IDX_STATE]);
        LocalDate date = LocalDate.parse(row[IDX_BOOKING_DATE]);
        String userEmail = row[IDX_USER_EMAIL];
        String tourID = row[IDX_TOUR_ID];

        Tour tour;
        try {
            tour = tourDAO.retrieveTourFromId(tourID);
        } catch (TourNotFoundException e) {
            throw new CSVPersistenceException(
                    "Cannot rebuild Tour from id in CSV: " + tourID, e);
        }

        Ticket t = new Ticket(id, date, st, userEmail);
        t.setTour(tour);
        return t;
    }

    private boolean sameTicketId(String[] row, String ticketId) {
        return row != null
                && row.length > IDX_TICKET_ID
                && ticketId != null
                && ticketId.equals(row[IDX_TICKET_ID]);
    }

    private boolean sameUserEmail(String[] row, String userEmail) {
        return row != null
                && row.length > IDX_USER_EMAIL
                && userEmail != null
                && userEmail.equalsIgnoreCase(row[IDX_USER_EMAIL]);
    }

    @FunctionalInterface
    private interface RowPredicate {
        boolean test(String[] row);
    }

    private static class CSVPersistenceException extends RuntimeException {
        public CSVPersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
