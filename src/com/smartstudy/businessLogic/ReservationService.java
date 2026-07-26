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
    private final AbandonmentReportDAO abandonmentReportDAO;

    public ReservationService(AccessSessionDAO accessSessionDAO, StudentDAO studentDAO, SeatDAO seatDAO, ReservationDAO reservationDAO, AbandonmentReportDAO abandonmentReportDAO) {
        this.accessSessionDAO = accessSessionDAO;
        this.studentDAO = studentDAO;
        this.seatDAO = seatDAO;
        this.reservationDAO = reservationDAO;
        this.abandonmentReportDAO = abandonmentReportDAO;
    }

    public Seat scanSeat(String qrCode, long studentId){
        if(qrCode == null || qrCode.isBlank()){
            throw new BusinessViolationException("Il qrCode è vuoto");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (!accessSessionDAO.hasActiveAccessSessionByStudent(studentId)) {
                throw new BusinessViolationException("L'utente non ha acceduto in una biblioteca");
            }
            if (studentDAO.getStudentById(studentId).isEmpty()) {
                throw new BusinessViolationException("L'utente non è registrato nel sistema");
            }
            return seatDAO.getSeatByQR(qrCode).orElseThrow(() -> new BusinessViolationException("Il qrcode non è associato a nessun posto"));
        });
    }
    public Reservation createReservation(long studentId, long seatId){
        return TransactionManager.executeInTransaction(() -> {
            AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId).orElseThrow(() -> new BusinessViolationException("L'utente non ha acceduto in una biblioteca"));
            Seat seat = seatDAO.getSeatById(seatId).orElseThrow(() -> new BusinessViolationException("Posto non trovato"));
            if(seat.isBroken()){
                throw new BusinessViolationException("Il posto indicato non può essere prenotato: è rotto");
            }
            if(reservationDAO.getActiveReservationBySeat(seat.getId()).isPresent()){
                throw new BusinessViolationException("Il posto ha già una prenotazione valida attiva");
            }
            if(reservationDAO.getActiveReservationByStudent(studentId).isPresent()){
                throw new BusinessViolationException("Lo studente ha già una prenotazione attiva");
            }
            if (seat.getStudyArea().getLibrary().getId() != accessSession.getLibrary().getId()) {
                throw new BusinessViolationException("Il posto selezionato risulta in una biblioteca diversa di quella della sessione");
            }
            Reservation newReservation = Reservation.start(accessSession, seat);
            seatDAO.update(seat);
            return reservationDAO.insert(newReservation);
        });
    }

    public void closeReservation(long reservationId, long studentId){
        TransactionManager.executeInTransaction(() -> {
            AccessSession accessSession = accessSessionDAO.getActiveAccessSessionByStudent(studentId).orElseThrow(() -> new BusinessViolationException("L'utente non ha un accesso valido alla biblioteca"));
            Reservation reservation = reservationDAO.getReservationById(reservationId).orElseThrow(() -> new BusinessViolationException("La prenotazione inserita non è valida"));
            if (reservation.getSession().getId() != accessSession.getId()) {
                throw new BusinessViolationException("La prenotazione non è associata alla sessione indicata");
            }
            ArrayList<AbandonmentReport> reports = reservation.close();
            for (AbandonmentReport report : reports) {
                abandonmentReportDAO.update(report);
            }
            seatDAO.update(reservation.getSeat());
            reservationDAO.update(reservation);
        });
    }

    public ArrayList<Reservation> getReservationHistory(long studentId){
        if(studentDAO.getStudentById(studentId).isEmpty()) {
            throw new BusinessViolationException("Studente non valido");
        }
        return reservationDAO.getReservationsByStudent(studentId);
    }

    public Reservation getActiveStudentReservation(long studentId){
        return reservationDAO.getActiveReservationByStudent(studentId).orElse(null);
    }

    public Reservation getReservationByReport(long reportId){
        return reservationDAO.getReservationByReport(reportId).orElseThrow(() -> new BusinessViolationException("Il report indicato non ha una reservation associata"));
    }
}
