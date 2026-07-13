package com.smartstudy.businessLogic;

import com.smartstudy.ORM.*;
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
        AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId);
        if(accessSession == null){
            throw new BusinessViolationException("L'utente non ha acceduto in una biblioteca");
        }
        if(!studentDAO.existsById(studentId)){
            throw new BusinessViolationException("L'utente non è registrato nel sistema");
        }
        return seatDAO.getSeatByQR(qrCode);
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
        Reservation res = reservationDAO.getActiveReservationBySeat(seatId);
        if(res != null){
            throw new BusinessViolationException("Il posto ha già una prenotazione valida attiva");
        }
        Reservation newReservation = Reservation.start(accessSessionId, seatId);
        reservationDAO.insert(newReservation);
        return newReservation;
    }

    public Reservation closeReservation(long reservationId, long accessSessionId){
        AccessSession accessSession = accessSessionDAO.getActiveAccessSessionById(accessSessionId);
        if(accessSession == null){
            throw new BusinessViolationException("L'utente non ha un accesso valido alla biblioteca");
        }
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if(reservation == null){
            throw new BusinessViolationException("La prenotazione inserita non è valida");
        }
        Library library = libraryDAO.getLibraryBySeat(reservation.getSeat());
        if(library == null){
            throw new BusinessViolationException("Impossibile trovare la libreria associata alla prenotazione");
        }
        if(accessSession.getLibraryId() != library.getId()){
            throw new BusinessViolationException("La prenotazione non è associata alla sessione indicata");
        }
        reservation.close();
        reservationDAO.update(reservation);
        return reservation;
    }

    public ArrayList<Reservation> getReservationHistory(long studentId){
        return reservationDAO.getReservationsByStudent(studentId);
    }

    public Reservation getReservationBySeat(long seatId){
        return reservationDAO.getActiveReservationBySeat(seatId);
    }
}
