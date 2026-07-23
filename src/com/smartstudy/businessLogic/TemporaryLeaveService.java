package com.smartstudy.businessLogic;

import com.smartstudy.domainModel.*;
import com.smartstudy.ORM.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;

import java.util.ArrayList;

public class TemporaryLeaveService {
    private final TemporaryLeaveDAO temporaryLeaveDAO;
    private final ReservationDAO reservationDAO;
    private final AccessSessionDAO accessSessionDAO;

    public TemporaryLeaveService(TemporaryLeaveDAO temporaryLeaveDAO, ReservationDAO reservationDAO, AccessSessionDAO accessSessionDAO) {
        this.temporaryLeaveDAO = temporaryLeaveDAO;
        this.reservationDAO = reservationDAO;
        this.accessSessionDAO = accessSessionDAO;
    }

    public TemporaryLeave createTemporaryLeave(long reservationId, long studentId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId);
        if(reservation == null || accessSession == null){
            throw new BusinessViolationException("L'utente o la prenotazione non sono stati inseriti correttamente");
        }
        if(accessSession.getId() != reservation.getSession().getId()){
            throw new BusinessViolationException("La sessione dello studente non combacia con quella della reservation");
        }
        TemporaryLeave temporaryLeave = reservation.addTemporaryLeave();
        return TransactionManager.executeInTransaction(() -> {
            reservationDAO.update(reservation);
            return temporaryLeaveDAO.insert(temporaryLeave, reservationId);
        });
    }

    public ArrayList<TemporaryLeave> getTemporaryLeavesByReservation(long reservationId, long studentId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId);
        if(reservation == null ||  accessSession == null){
            throw new BusinessViolationException("La prenotazione o la sessione non sono validi");
        }
        if(accessSession.getId() != reservation.getSession().getId()){
            throw new BusinessViolationException("La sessione indicata non coincide con quella della prenotazione");
        }
        return reservation.getTemporaryLeaves();
    }
}
