package com.smartstudy.businessLogic;

import com.smartstudy.db.TransactionManager;
import com.smartstudy.domainModel.*;
import com.smartstudy.ORM.*;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.exceptions.DomainViolationException;

import java.util.ArrayList;

public class ReportService {
    private final ReservationDAO reservationDAO;
    private final StudentDAO studentDAO;
    private final TemporaryLeaveDAO temporaryLeaveDAO;
    private final AbandonmentReportDAO reportDAO;
    private final AccessSessionDAO accessSessionDAO;
    private final AbandonmentReportDAO abandonmentReportDAO;
    private final AdminDAO adminDAO;
    private final LibraryDAO libraryDAO;

    public ReportService(ReservationDAO reservationDAO, StudentDAO studentDAO, TemporaryLeaveDAO temporaryLeaveDAO, AbandonmentReportDAO reportDAO, AbandonmentReportDAO abandonmentReportDAO, AccessSessionDAO accessSessionDAO, SeatDAO seatDAO, LibraryDAO libraryDAO, AbandonmentReportDAO abandonmentReportDAO1, AdminDAO adminDAO, LibraryDAO libraryDAO1) {
        this.reservationDAO = reservationDAO;
        this.studentDAO = studentDAO;
        this.temporaryLeaveDAO = temporaryLeaveDAO;
        this.reportDAO = reportDAO;
        this.accessSessionDAO = accessSessionDAO;
        this.abandonmentReportDAO = abandonmentReportDAO1;
        this.adminDAO = adminDAO;
        this.libraryDAO = libraryDAO1;
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
            AccessSession asReservation = accessSessionDAO.getActiveAccessSessionById(reservationId);
            if (asReservation.getLibraryId() != asStudent.getLibraryId()) {
                throw new BusinessViolationException("Lo studente non può fare report al posto di questa biblioteca");
            }
            if (temporaryLeaveDAO.hasActiveTemporaryLeave(reservationId)) {
                throw new BusinessViolationException("La prenotazione ha una temporary leave valida");
            }
            if (reportDAO.existsOpenReportByReservation(reservationId)) {
                throw new BusinessViolationException("La prenotazione ha già un report associato");
            }
            AbandonmentReport report = AbandonmentReport.open(description, reservationId, studentId);
            return abandonmentReportDAO.insert(report);
        });
    }

    public AbandonmentReport takeInCharge(long adminId, long reportId){
        AbandonmentReport report = abandonmentReportDAO.getReportById(reportId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(admin == null || report == null){
            throw new BusinessViolationException("L'admin o la reservation non sono validi");
        }
        if(report.getAdminId() != null){
            throw new BusinessViolationException("Il report è già stato preso in carico");
        }
        Reservation reservation =  reservationDAO.getReservationById(report.getReservationId());
        if(reservation == null){
            throw new BusinessViolationException("Il report non corrisponde a nessuna prenotazione");
        }
        Library library = libraryDAO.getLibraryBySeat(reservation.getSeat());
        if(library == null){
            throw new BusinessViolationException("La libreria associata alla postazione non esiste");
        }
        return TransactionManager.executeInTransaction(() -> {
            if (library.getId() != admin.getLibraryId()) {
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.takeInCharge(admin.getId());
            reportDAO.update(report);
            return report;
        });
    }

    public AbandonmentReport confirm(long adminId, long reportId){
        AbandonmentReport report = abandonmentReportDAO.getReportById(reportId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(admin == null || report == null){
            throw new BusinessViolationException("L'admin o la reservation non sono validi");
        }
        Reservation reservation =  reservationDAO.getReservationById(report.getReservationId());
        if(reservation == null){
            throw new BusinessViolationException("Il report non corrisponde a nessuna prenotazione");
        }
        return TransactionManager.executeInTransaction(() -> {
            Library library = libraryDAO.getLibraryBySeat(reservation.getSeat());
            if(library == null){
                throw new BusinessViolationException("La libreria associata alla postazione non esiste");
            }
            if(library.getId() != admin.getLibraryId()){
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.confirm(admin.getId());
            reportDAO.update(report);
            reservation.close();
            reservationDAO.update(reservation);
            return report;
        });
    }

    public AbandonmentReport reject(long adminId, long reportId){
        AbandonmentReport report = abandonmentReportDAO.getReportById(reportId);
        Admin admin = adminDAO.getAdminById(adminId);
        if(admin == null || report == null){
            throw new BusinessViolationException("L'admin o la reservation non sono validi");
        }
        Reservation reservation =  reservationDAO.getReservationById(report.getReservationId());
        if(reservation == null){
            throw new BusinessViolationException("Il report non corrisponde a nessuna prenotazione");
        }
        return TransactionManager.executeInTransaction(() -> {
            Library library = libraryDAO.getLibraryBySeat(reservation.getSeat());
            if (library == null) {
                throw new BusinessViolationException("La libreria associata alla postazione non esiste");
            }
            if (library.getId() != admin.getLibraryId()) {
                throw new BusinessViolationException("Il report non corrisponde alla biblioteca gestita dall'admin");
            }
            report.reject(admin.getId());
            reportDAO.update(report);
            return report;
        });
    }

    public ArrayList<AbandonmentReport> getOpenReportsByLibrary(long libraryId){
        return abandonmentReportDAO.getReportsByLibrary(libraryId);
    }

    public ArrayList<AbandonmentReport> getReportsInChargeByAdmin(long adminId){
        if(!adminDAO.existsById(adminId)){
            throw new DomainViolationException("L'admin non è registrato nel sistema");
        }
        return abandonmentReportDAO.getInProgressReportsByAdmin(adminId);
    }

    public ArrayList<AbandonmentReport> getClosedReportsByAdmin(long adminId){
        if(!adminDAO.existsById(adminId)){
            throw new DomainViolationException("L'admin non è registrato nel sistema");
        }
        return abandonmentReportDAO.getClosedReportsByAdmin(adminId);
    }

    public ArrayList<AbandonmentReport> getOpenReportsByStudent(long studentId){
        if(!studentDAO.existsById(studentId)){
            throw new DomainViolationException("Lo studente non è registrato nel sistema");
        }
        return abandonmentReportDAO.getOpenReportsByStudent(studentId);
    }

}
