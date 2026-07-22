package com.smartstudy.businessLogic;

import com.smartstudy.db.TransactionManager;
import com.smartstudy.domainModel.*;
import com.smartstudy.ORM.*;
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

    public AbandonmentReport createReport(long studentId, String description, long reservationId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if(reservation == null)
            throw new BusinessViolationException("La postazione non ha prenotazioni associate");
        Student student = studentDAO.getStudentById(studentId);
        if(student == null)
            throw new BusinessViolationException("Lo studente indicato non è stato trovato");
        AccessSession asStudent = accessSessionDAO.getActiveAccessSessionByStudent(studentId);
        if(asStudent == null){
            throw new BusinessViolationException("Lo studente non ha una access session attiva");
        }
        return TransactionManager.executeInTransaction(() -> {
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
            reservation.addAbandonmentReport(report);
            reservationDAO.update(reservation);
            abandonmentReportDAO.insert(report, reservation.getId());
            return report;
        });
    }

    public AbandonmentReport takeInCharge(long adminId, long reportId){
        AbandonmentReport report = abandonmentReportDAO.getReportById(reportId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(admin == null || report == null){
            throw new BusinessViolationException("L'admin o la reservation non sono validi");
        }
        if(report.getAdmin() != null){
            throw new BusinessViolationException("Il report è già stato preso in carico");
        }
        Reservation reservation =  reservationDAO.getReservationByReport(report.getId());
        if(reservation == null){
            throw new BusinessViolationException("Il report non corrisponde a nessuna prenotazione");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (reservation.getSeat().getStudyArea().getLibrary().hasAdmin(admin.getId())) {
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.takeInCharge(admin);
            abandonmentReportDAO.update(report);
            return report;
        });
    }

    public AbandonmentReport confirm(long adminId, long reportId){
        AbandonmentReport report = abandonmentReportDAO.getReportById(reportId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(admin == null || report == null){
            throw new BusinessViolationException("L'admin o la reservation non sono validi");
        }
        Reservation reservation =  reservationDAO.getReservationByReport(report.getId());
        if(reservation == null){
            throw new BusinessViolationException("Il report non corrisponde a nessuna prenotazione");
        }
        return TransactionManager.executeInTransaction(() -> {
            if(reservation.getSeat().getStudyArea().getLibrary().hasAdmin(admin.getId())){
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.confirm(admin);
            abandonmentReportDAO.update(report);
            reservation.close();
            reservationDAO.update(reservation);
            Seat seat = reservation.getSeat();
            seat.free();
            seatDAO.update(seat);
            return report;
        });
    }

    public AbandonmentReport reject(long adminId, long reportId){
        AbandonmentReport report = abandonmentReportDAO.getReportById(reportId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(admin == null || report == null){
            throw new BusinessViolationException("L'admin o la reservation non sono validi");
        }
        Reservation reservation =  reservationDAO.getReservationByReport(report.getId());
        if(reservation == null){
            throw new BusinessViolationException("Il report non corrisponde a nessuna prenotazione");
        }
        return TransactionManager.executeInTransaction(() -> {
            if(reservation.getSeat().getStudyArea().getLibrary().hasAdmin(admin.getId())){
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.reject(admin);
            abandonmentReportDAO.update(report);
            return report;
        });
    }

    public ArrayList<AbandonmentReport> getOpenReportsByLibrary(long libraryId){
        Library library = libraryDAO.getLibraryById(libraryId);
        if(library == null){
            throw new BusinessViolationException("La libreria non esiste");
        }
        return abandonmentReportDAO.getReportsByLibrary(libraryId);
    }

    public ArrayList<AbandonmentReport> getReportsInChargeByAdmin(long adminId){
        if(!adminDAO.existsById(adminId)){
            throw new BusinessViolationException("L'admin non è registrato nel sistema");
        }
        return abandonmentReportDAO.getInProgressReportsByAdmin(adminId);
    }

    public ArrayList<AbandonmentReport> getClosedReportsByAdmin(long adminId){
        if(!adminDAO.existsById(adminId)){
            throw new BusinessViolationException("L'admin non è registrato nel sistema");
        }
        return abandonmentReportDAO.getClosedReportsByAdmin(adminId);
    }

    public ArrayList<AbandonmentReport> getOpenReportsByStudent(long studentId){
        if(!studentDAO.existsById(studentId)){
            throw new BusinessViolationException("Lo studente non è registrato nel sistema");
        }
        return abandonmentReportDAO.getOpenReportsByStudent(studentId);
    }

}
