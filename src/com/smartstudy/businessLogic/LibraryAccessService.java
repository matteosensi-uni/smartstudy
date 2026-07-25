package com.smartstudy.businessLogic;

import com.smartstudy.ORM.*;
import com.smartstudy.domainModel.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;


public class LibraryAccessService {
    private final AdminDAO adminDAO;
    private final AccessSessionDAO accessSessionDAO;
    private final StudentDAO studentDAO;
    private final ReservationDAO reservationDAO;
    private final LibraryDAO libraryDAO;
    private final SeatDAO seatDAO;

    public LibraryAccessService(AdminDAO adminDAO, AccessSessionDAO accessSessionDAO, StudentDAO studentDAO, ReservationDAO reservationDAO, LibraryDAO libraryDAO, SeatDAO seatDAO) {
        this.adminDAO = adminDAO;
        this.accessSessionDAO = accessSessionDAO;
        this.studentDAO = studentDAO;
        this.reservationDAO = reservationDAO;
        this.libraryDAO = libraryDAO;
        this.seatDAO = seatDAO;
    }

    public boolean isStudentPresent(long studentId){
        return accessSessionDAO.hasActiveAccessSessionByStudent(studentId);
    }

    public boolean toggleUserPresence(long userId, long libraryId){
        Library library = libraryDAO.getLibraryById(libraryId);
        if(library == null){
            throw new BusinessViolationException("La biblioteca inserita non è valida");
        }
        if(adminDAO.existsById(userId)){
            Admin admin = adminDAO.getAdminById(userId);
            return toggleAdminPresence(admin,library);
        }else if(studentDAO.existsById(userId)){
            Student student = studentDAO.getStudentById(userId);
            return toggleStudentAccess(student, library);
        }else{
            throw new BusinessViolationException("L'utente non è stato riconosciuto dal sistema");
        }
    }

    private boolean toggleAdminPresence(Admin admin, Library library){
        if(!library.hasAdmin(admin.getId()))
            throw new BusinessViolationException("L'admin non può gestire questa biblioteca");
        return TransactionManager.executeInTransaction(() -> {
            if(!admin.isPresent() && !library.isOpen()){
                throw new BusinessViolationException("La biblioteca è chiusa, l'admin non può entrare");
            }
            admin.togglePresence();
            adminDAO.update(admin);
            return admin.isPresent();
        });
    }

    private boolean toggleStudentAccess(Student student, Library library) {
        return TransactionManager.executeInTransaction(() -> {
            if (accessSessionDAO.hasActiveAccessSessionByStudent(student.getId())) { //lo studente esce dalla biblioteca
                AccessSession as = accessSessionDAO.getActiveAccessSessionByStudent(student.getId());
                if(as.getLibrary().getId() != library.getId()){
                    throw new BusinessViolationException("La libreria non combacia con quella della sessione");
                }
                Reservation reservation = reservationDAO.getActiveReservationByStudent(student.getId());
                if(reservation != null){
                    reservation.close();
                    reservationDAO.update(reservation);
                    seatDAO.update(reservation.getSeat());
                }
                as.closeSession();
                accessSessionDAO.update(as);
                return false;
            } else { //lo studente entra in biblioteca
                if (!student.isCardActive()) {
                    throw new BusinessViolationException("La carta dello studente non è attiva");
                }

                AccessSession as = AccessSession.startSession(library, student);
                accessSessionDAO.insert(as);
                return true;
            }
        });

    }
}
