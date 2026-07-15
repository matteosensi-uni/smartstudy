package com.smartstudy.businessLogic;

import com.smartstudy.ORM.ReservationDAO;
import com.smartstudy.domainModel.AccessSession;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.Student;
import com.smartstudy.ORM.AccessSessionDAO;
import com.smartstudy.ORM.AdminDAO;
import com.smartstudy.ORM.StudentDAO;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;


public class LibraryAccessService {
    private final AdminDAO adminDAO;
    private final AccessSessionDAO accessSessionDAO;
    private final StudentDAO studentDAO;
    private final ReservationDAO reservationDAO;

    public LibraryAccessService(AdminDAO adminDAO, AccessSessionDAO accessSessionDAO, StudentDAO studentDAO, ReservationDAO reservationDAO) {
        this.adminDAO = adminDAO;
        this.accessSessionDAO = accessSessionDAO;
        this.studentDAO = studentDAO;
        this.reservationDAO = reservationDAO;
    }

    public void toggleUserPresence(long userId, long libraryId){
        if(adminDAO.existsById(userId)){
            Admin admin = adminDAO.getAdminById(userId);
            toggleAdminPresence(admin,libraryId);
        }else if(studentDAO.existsById(userId)){
            Student student = studentDAO.getStudentById(userId);
            toggleStudentAccess(student, libraryId);
        }else{
            throw new BusinessViolationException("L'utente non è stato riconosciuto dal sistema");
        }
    }

    private void toggleAdminPresence(Admin admin, long libraryId){
        if(admin == null)
            throw new BusinessViolationException("Admin non trovato");
        if(admin.getLibraryId() != libraryId)
            throw new BusinessViolationException("L'admin non può gestire questa biblioteca");
        TransactionManager.executeInTransaction(() -> {
            admin.togglePresence();
            adminDAO.update(admin);
        });
    }

    private void toggleStudentAccess(Student student, long libraryId) {
        if (student == null) {
            throw new BusinessViolationException("Lo studente non risulta nel sistema");
        }
        TransactionManager.executeInTransaction(() -> {
            if (accessSessionDAO.hasActiveAccessSessionByStudent(student.getId())) { //lo studente esce dalla biblioteca
                AccessSession as = accessSessionDAO.getActiveAccessSessionByStudent(student.getId());
                if(as.getLibraryId() != libraryId){
                    throw new BusinessViolationException("La libreria non combacia con quella della sessione");
                }
                Reservation reservation = reservationDAO.getActiveReservationByStudent(student.getId());
                if(reservation != null){
                    reservation.close();
                    reservationDAO.update(reservation);
                }
                as.closeSession();
                accessSessionDAO.update(as);
            } else { //lo studente entra in biblioteca
                if (!student.isCardActive()) {
                    throw new BusinessViolationException("La carta dello studente non è attiva");
                }
                AccessSession as = AccessSession.startSession(libraryId, student.getId());
                accessSessionDAO.insert(as);
            }
        });

    }
}
