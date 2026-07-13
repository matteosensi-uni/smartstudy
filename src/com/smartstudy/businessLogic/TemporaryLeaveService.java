package com.smartstudy.businessLogic;

import com.smartstudy.domainModel.*;
import com.smartstudy.ORM.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;

import java.util.ArrayList;

public class TemporaryLeaveService {
    private final TemporaryLeaveDAO temporaryLeaveDAO;
    private final ReservationDAO reservationDAO;
    private final TimePolicyDAO  timePolicyDAO;
    private final AccessSessionDAO accessSessionDAO;

    public TemporaryLeaveService(TemporaryLeaveDAO temporaryLeaveDAO, ReservationDAO reservationDAO, TimePolicyDAO timePolicyDAO, AccessSessionDAO accessSessionDAO) {
        this.temporaryLeaveDAO = temporaryLeaveDAO;
        this.reservationDAO = reservationDAO;
        this.timePolicyDAO = timePolicyDAO;
        this.accessSessionDAO = accessSessionDAO;
    }

    public TemporaryLeave createTemporaryLeave(long reservationId, long studentId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId);
        if(reservation == null || accessSession == null){
            throw new BusinessViolationException("L'utente o la prenotazione non sono stati inseriti correttamente");
        }
        if(!reservation.isActive()){
            throw new BusinessViolationException("La prenotazione inserita non è attiva");
        }
        if(accessSession.getId() != reservation.getId()){
            throw new BusinessViolationException("La sessione dello studente non combacia con quella della reservation");
        }
        TimePolicy timePolicy = timePolicyDAO.getTimePolicyBySeat(reservation.getSeat());
        if(timePolicy == null){
            throw new BusinessViolationException("Non è stata trovate la regola associata al posto");
        }
        TemporaryLeave temporaryLeave = TemporaryLeave.create(timePolicy.getMaxTemporaryLeaveMin(), reservation.getId());
        TransactionManager.executeInTransaction(() -> { //si usa il transaction manager per evitare uno stato inconsistente del database
            if (temporaryLeaveDAO.hasActiveTemporaryLeave(reservation.getId())) {
                throw new BusinessViolationException("Lo studente ha gia una temporary leave attiva");
            }
            if (timePolicy.reachedLimit(temporaryLeaveDAO.countTemporaryLeavesByReservation(reservation.getId()))) {
                throw new BusinessViolationException("Sono state raggiunte il numero massimo di pause");
            }
            reservation.markTemporarilyLeft();
            reservationDAO.update(reservation);
            temporaryLeave.setId(temporaryLeaveDAO.insert(temporaryLeave));
        });
        return temporaryLeave;
    }

    public  void checkExpiredTemporaryLeaves() {
        ArrayList<TemporaryLeave> leaves = temporaryLeaveDAO.getExpiredTemporaryLeaves();
        for(TemporaryLeave leave : leaves){
            Reservation r = reservationDAO.getReservationById(leave.getReservationId());
            r.markActive(); // le reservation tornano segnalabili
            reservationDAO.update(r);
        }
    }

    public ArrayList<TemporaryLeave> getTemporaryLeavesByReservation(long reservationId, long studentId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId);
        if(reservation == null ||  accessSession == null){
            throw new BusinessViolationException("La prenotazione o la sessione non sono validi");
        }
        if(accessSession.getId() != reservation.getSessionId()){
            throw new BusinessViolationException("La sessione indicata non coincide con quella della prenotazione");
        }
        return temporaryLeaveDAO.getTemporaryLeavesByReservation(reservation.getId());
    }
}
