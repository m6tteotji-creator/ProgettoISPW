package com.example.onetour.dao;

import com.example.onetour.enumeration.TicketState;
import com.example.onetour.exception.DuplicateTicketException;
import com.example.onetour.exception.TicketNotFoundException;
import com.example.onetour.model.Ticket;

import java.util.List;

public abstract class TicketDAO {

    public abstract void create(Ticket ticket)
            throws DuplicateTicketException;

    public abstract List<Ticket> retrieveByUser(String userEmail)
            throws TicketNotFoundException;

    public abstract List<Ticket> retrievePendingByGuide(String guideEmail)
            throws TicketNotFoundException;

    public abstract List<Ticket> retrieveByGuide(String guideEmail) throws TicketNotFoundException;

    public abstract void modifyState(String ticketID, TicketState newState)
            throws TicketNotFoundException;

}
