package com.smartstudy.businessLogic;

import com.smartstudy.domainModel.*;
import com.smartstudy.orm.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;

public class TemporaryLeaveService {
    private final TemporaryLeaveDAO temporaryLeaveDAO;
    private final ReservationDAO reservationDAO;
    private final AccessSessionDAO accessSessionDAO;

    public TemporaryLeaveService(TemporaryLeaveDAO temporaryLeaveDAO, ReservationDAO reservationDAO, AccessSessionDAO accessSessionDAO) {
        this.temporaryLeaveDAO = temporaryLeaveDAO;
        this.reservationDAO = reservationDAO;
        this.accessSessionDAO = accessSessionDAO;
    }

    public Reservation createTemporaryLeave(long reservationId, long studentId) {
        return TransactionManager.executeInTransaction(() -> {
            Reservation reservation = reservationDAO.getReservationById(reservationId).orElseThrow(() -> new BusinessViolationException("La prenotazione inserita non è valida"));
            AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId).orElseThrow(() -> new BusinessViolationException("L'utente non ha una sessione di accesso valida"));
            if(accessSession.getId() != reservation.getSession().getId()){
                throw new BusinessViolationException("La sessione dello studente non combacia con quella della reservation");
            }
            TemporaryLeave temporaryLeave = reservation.addTemporaryLeave();
            reservationDAO.update(reservation);
            temporaryLeaveDAO.insert(temporaryLeave, reservationId);
            return reservationDAO.getReservationById(reservationId).orElseThrow(() -> new BusinessViolationException("La prenotazione inserita non è valida"));
        });
    }
}
