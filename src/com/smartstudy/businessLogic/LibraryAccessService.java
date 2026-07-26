package com.smartstudy.businessLogic;

import com.smartstudy.ORM.*;
import com.smartstudy.domainModel.*;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;

import java.util.Optional;


public class LibraryAccessService {
    private final AdminDAO adminDAO;
    private final AccessSessionDAO accessSessionDAO;
    private final StudentDAO studentDAO;
    private final LibraryDAO libraryDAO;

    public LibraryAccessService(AdminDAO adminDAO, AccessSessionDAO accessSessionDAO, StudentDAO studentDAO, LibraryDAO libraryDAO) {
        this.adminDAO = adminDAO;
        this.accessSessionDAO = accessSessionDAO;
        this.studentDAO = studentDAO;
        this.libraryDAO = libraryDAO;
    }

    public boolean isStudentPresent(long studentId){
        return accessSessionDAO.hasActiveAccessSessionByStudent(studentId);
    }

    public boolean toggleUserPresence(long userId, long libraryId){
        return TransactionManager.executeInTransaction(() -> {
            Library library = libraryDAO.getLibraryById(libraryId).orElseThrow(() -> new BusinessViolationException("La libreria inserita non esiste"));
            Optional<Admin> admin = adminDAO.getAdminById(userId);
            if (admin.isPresent())
                return toggleAdminPresence(admin.get(), library);
            Optional<Student> student = studentDAO.getStudentById(userId);
            if (student.isPresent())
                return toggleStudentAccess(student.get(), library);
            throw new BusinessViolationException("L'utente non è stato riconosciuto dal sistema");
        });
    }

    private boolean toggleAdminPresence(Admin admin, Library library){
        if(!library.hasAdmin(admin.getId()))
            throw new BusinessViolationException("L'admin non può gestire questa biblioteca");
        if(!admin.isPresent() && !library.isOpen()){
            throw new BusinessViolationException("La biblioteca è chiusa, l'admin non può entrare");
        }
        admin.togglePresence();
        adminDAO.update(admin);
        return admin.isPresent();
    }

    private boolean toggleStudentAccess(Student student, Library library) {
        if (accessSessionDAO.hasActiveAccessSessionByStudent(student.getId())) { //lo studente esce dalla biblioteca
            AccessSession as = accessSessionDAO.getActiveAccessSessionByStudent(student.getId()).orElseThrow(() -> new BusinessViolationException("Sessione di accesso non trovata"));
            if(as.getLibrary().getId() != library.getId()){
                throw new BusinessViolationException("La libreria non combacia con quella della sessione");
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
    }
}
