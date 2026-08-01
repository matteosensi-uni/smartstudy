package com.smartstudy.businessLogic;

import com.smartstudy.db.TransactionManager;
import com.smartstudy.domainModel.*;
import com.smartstudy.orm.*;
import com.smartstudy.exceptions.BusinessViolationException;

import java.util.ArrayList;

public class ReportService {
    private final ReservationDAO reservationDAO;
    private final StudentDAO studentDAO;
    private final AccessSessionDAO accessSessionDAO;
    private final AbandonmentReportDAO abandonmentReportDAO;
    private final AdminDAO adminDAO;
    private final LibraryDAO libraryDAO;
    private final SeatDAO seatDAO;

    public ReportService(ReservationDAO reservationDAO, StudentDAO studentDAO, AbandonmentReportDAO abandonmentReportDAO, AccessSessionDAO accessSessionDAO, LibraryDAO libraryDAO, AdminDAO adminDAO, SeatDAO seatDAO) {
        this.reservationDAO = reservationDAO;
        this.studentDAO = studentDAO;
        this.accessSessionDAO = accessSessionDAO;
        this.abandonmentReportDAO = abandonmentReportDAO;
        this.adminDAO = adminDAO;
        this.libraryDAO = libraryDAO;
        this.seatDAO = seatDAO;
    }

    public void createReport(long studentId, String description, long seatId) {
        TransactionManager.executeInTransaction(() -> {
            Reservation reservation = reservationDAO.getActiveReservationBySeat(seatId).orElseThrow(() -> new BusinessViolationException("La postazione non ha prenotazioni associate"));
            Student student = studentDAO.getStudentById(studentId).orElseThrow(() -> new BusinessViolationException("Studente non valido"));
            AccessSession asStudent = accessSessionDAO.getActiveAccessSessionByStudent(studentId).orElseThrow(() -> new BusinessViolationException("Lo studente non ha una access session attiva"));
            if (reservation.getSeat().getStudyArea().getLibrary().getId() != asStudent.getLibrary().getId()) {
                throw new BusinessViolationException("Lo studente non può fare report al posto di questa biblioteca");
            }
            if (reservation.hasValidTemporaryLeave()) {
                throw new BusinessViolationException("La prenotazione ha una temporary leave valida");
            }
            if (reservation.hasActiveReport()) {
                throw new BusinessViolationException("La prenotazione ha già un report associato");
            }
            AbandonmentReport report = AbandonmentReport.open(description, student);
            reservation.addAbandonmentReport(abandonmentReportDAO.insert(report, reservation.getId()));
            reservationDAO.update(reservation);
        });
    }

    public void takeInCharge(long adminId, long reportId){
        TransactionManager.executeInTransaction(() -> {
            AbandonmentReport report = abandonmentReportDAO.getReportById(reportId).orElseThrow(() -> new BusinessViolationException("Il report non è valido"));
            Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
            Reservation reservation =  reservationDAO.getReservationByReport(report.getId()).orElseThrow(() -> new BusinessViolationException("Il report non corrisponde a nessuna prenotazione"));
            if (!reservation.getSeat().getStudyArea().getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.takeInCharge(admin);
            abandonmentReportDAO.update(report);
        });
    }

    public void confirm(long adminId, long reportId){
        TransactionManager.executeInTransaction(() -> {
            AbandonmentReport report = abandonmentReportDAO.getReportById(reportId).orElseThrow(() -> new BusinessViolationException("Il report non è valido"));
            Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
            Reservation reservation =  reservationDAO.getReservationByReport(report.getId()).orElseThrow(() -> new BusinessViolationException("Il report non corrisponde a nessuna prenotazione"));
            if(!reservation.getSeat().getStudyArea().getLibrary().hasAdmin(admin.getId())){
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.confirm(admin);
            abandonmentReportDAO.update(report);
            reservation.close();
            reservationDAO.update(reservation);
            seatDAO.update(reservation.getSeat());
        });
    }

    public void reject(long adminId, long reportId){
        TransactionManager.executeInTransaction(() -> {
            AbandonmentReport report = abandonmentReportDAO.getReportById(reportId).orElseThrow(() -> new BusinessViolationException("Il report non è valido"));
            Admin admin = adminDAO.getAdminById(adminId).orElseThrow(() ->  new BusinessViolationException("Admin non trovato"));
            Reservation reservation =  reservationDAO.getReservationByReport(report.getId()).orElseThrow(() -> new BusinessViolationException("Il report non corrisponde a nessuna prenotazione"));
            if(!reservation.getSeat().getStudyArea().getLibrary().hasAdmin(admin.getId())){
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.reject(admin);
            abandonmentReportDAO.update(report);
        });
    }

    public ArrayList<AbandonmentReport> getOpenReportsByAdmin(long adminId){
        Library library = libraryDAO.getLibraryByAdmin(adminId).orElseThrow(() -> new BusinessViolationException("Non è stata trovata una biblioteca associata all'admin"));
        return abandonmentReportDAO.getReportsByLibrary(library.getId());
    }

    public ArrayList<AbandonmentReport> getReportsInChargeByAdmin(long adminId){
        if(adminDAO.getAdminById(adminId).isEmpty()){
            throw new BusinessViolationException("L'admin non è registrato nel sistema");
        }
        return abandonmentReportDAO.getInProgressReportsByAdmin(adminId);
    }

    public ArrayList<AbandonmentReport> getClosedReportsByAdmin(long adminId){
        if(adminDAO.getAdminById(adminId).isEmpty()){
            throw new BusinessViolationException("L'admin non è registrato nel sistema");
        }
        return abandonmentReportDAO.getClosedReportsByAdmin(adminId);
    }

    public ArrayList<AbandonmentReport> getOpenReportsByStudent(long studentId){
        if(studentDAO.getStudentById(studentId).isEmpty()){
            throw new BusinessViolationException("Lo studente non è registrato nel sistema");
        }
        return abandonmentReportDAO.getOpenReportsByStudent(studentId);
    }

}
