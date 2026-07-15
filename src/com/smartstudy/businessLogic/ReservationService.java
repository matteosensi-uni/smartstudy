package com.smartstudy.businessLogic;

import com.smartstudy.ORM.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.domainModel.*;
import com.smartstudy.exceptions.BusinessViolationException;

import java.util.ArrayList;

public class ReservationService {
    private final AccessSessionDAO accessSessionDAO;
    private final StudentDAO studentDAO;
    private final SeatDAO seatDAO;
    private final ReservationDAO reservationDAO;
    private final LibraryDAO libraryDAO;

    public ReservationService(AccessSessionDAO accessSessionDAO, StudentDAO studentDAO, SeatDAO seatDAO, ReservationDAO reservationDAO, LibraryDAO libraryDAO, TemporaryLeaveDAO temporaryLeaveDAO) {
        this.accessSessionDAO = accessSessionDAO;
        this.studentDAO = studentDAO;
        this.seatDAO = seatDAO;
        this.reservationDAO = reservationDAO;
        this.libraryDAO = libraryDAO;
    }

    public Seat scanSeat(String qrCode, long studentId){
        return TransactionManager.executeInTransaction(() -> {
            if (!accessSessionDAO.hasActiveAccessSessionByStudent(studentId)) {
                throw new BusinessViolationException("L'utente non ha acceduto in una biblioteca");
            }
            if (!studentDAO.existsById(studentId)) {
                throw new BusinessViolationException("L'utente non è registrato nel sistema");
            }
            return seatDAO.getSeatByQR(qrCode);
        });
    }
    public Reservation createReservation(long accessSessionId, long seatId){
        AccessSession accessSession = accessSessionDAO.getActiveAccessSessionById(accessSessionId);
        if(accessSession == null){
            throw new BusinessViolationException("L'utente non ha acceduto in una biblioteca");
        }
        Seat seat = seatDAO.getSeatById(seatId);
        if(seat == null){
            throw new BusinessViolationException("Il posto indicato non risulta valido");
        }
        if(seat.isBroken()){
            throw new BusinessViolationException("Il posto indicato non può essere prenotato: è rotto");
        }
        return TransactionManager.executeInTransaction(() -> {
            Reservation res = reservationDAO.getActiveReservationBySeat(seatId);
            if (res != null) {
                throw new BusinessViolationException("Il posto ha già una prenotazione valida attiva");
            }
            Library library = libraryDAO.getLibraryBySeat(seatId);
            if (library == null) {
                throw new BusinessViolationException("Il posto indicato non ha una biblioteca associata");
            }
            if (library.getId() != accessSession.getLibraryId()) {
                throw new BusinessViolationException("Il posto selezionato risulta in una biblioteca diversa di quella della sessione");
            }
            seat.occupy();
            seatDAO.update(seat);
            Reservation newReservation = Reservation.start(accessSessionId, seatId);
            reservationDAO.insert(newReservation);
            return newReservation;
        });
    }

    public Reservation closeReservation(long reservationId, long accessSessionId){
        return TransactionManager.executeInTransaction(() -> {
            AccessSession accessSession = accessSessionDAO.getActiveAccessSessionById(accessSessionId);
            if (accessSession == null) {
                throw new BusinessViolationException("L'utente non ha un accesso valido alla biblioteca");
            }
            Reservation reservation = reservationDAO.getReservationById(reservationId);
            if (reservation == null) {
                throw new BusinessViolationException("La prenotazione inserita non è valida");
            }
            if (reservation.getSessionId() != accessSessionId) {
                throw new BusinessViolationException("La prenotazione non è associata alla sessione indicata");
            }
            Seat seat = seatDAO.getSeatById(reservation.getSeatId());
            if(seat == null){
                throw new BusinessViolationException("Il posto indicato non risulta valido");
            }
            seat.free();
            seatDAO.update(seat);
            reservation.close();
            reservationDAO.update(reservation);
            return reservation;
        });
    }

    public ArrayList<Reservation> getReservationHistory(long studentId){
        if(!studentDAO.existsById(studentId)) {
            throw new BusinessViolationException("Studente non valido");
        }
        return reservationDAO.getReservationsByStudent(studentId);
    }

    public Reservation getReservationBySeat(long seatId){
        Seat seat = seatDAO.getSeatById(seatId);
        if(seat == null){
            throw new BusinessViolationException("Il posto indicato non risulta valida");
        }
        return reservationDAO.getActiveReservationBySeat(seatId);
    }

    public boolean existReservationBySeat(long seatId){
        return  reservationDAO.existReservationBySeat(seatId);
    }
}
