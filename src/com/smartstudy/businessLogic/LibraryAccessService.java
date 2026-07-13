package com.smartstudy.businessLogic;

import com.smartstudy.domainModel.AccessSession;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Student;
import com.smartstudy.ORM.AccessSessionDAO;
import com.smartstudy.ORM.AdminDAO;
import com.smartstudy.ORM.StudentDAO;
import com.smartstudy.db.TransactionManager;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.SQLException;

public class LibraryAccessService {
    private final AdminDAO adminDAO;
    private final AccessSessionDAO accessSessionDAO;
    private final StudentDAO studentDAO;

    public LibraryAccessService(AdminDAO adminDAO, AccessSessionDAO accessSessionDAO, StudentDAO studentDAO) {
        this.adminDAO = adminDAO;
        this.accessSessionDAO = accessSessionDAO;
        this.studentDAO = studentDAO;
    }

    public void toggleAdminPresence(long adminId, long libraryId){
        Admin admin = adminDAO.getAdminById(adminId);
        if(admin == null)
            throw new BusinessViolationException("Admin non trovato");
        if(admin.getLibraryId() != libraryId)
            throw new BusinessViolationException("L'admin non può gestire questa biblioteca");
        TransactionManager.executeInTransaction(() -> {
            if(!admin.isPresent()) {
                admin.accessLibrary();
            }else{
                admin.leaveLibrary();
            }
            adminDAO.update(admin);
        });
    }

    public void toggleStudentAccess(long studentId, long libraryId) {
        TransactionManager.executeInTransaction(() -> {
            if (accessSessionDAO.hasActiveAccessSessionByStudent(studentId)) { //lo studente esce dalla biblioteca
                AccessSession as = accessSessionDAO.getActiveAccessSessionByStudent(studentId);
                as.closeSession(studentId, libraryId);
                accessSessionDAO.update(as);
            } else { //lo studente entra in biblioteca
                Student student = studentDAO.getStudentById(studentId);
                if (student == null) {
                    throw new BusinessViolationException("Lo studente non risulta nel sistema");
                }
                if (!student.isCardActive()) {
                    throw new BusinessViolationException("La carta dello studente non è attiva");
                }
                AccessSession as = AccessSession.startSession(libraryId, studentId);
                accessSessionDAO.insert(as);
            }
        });

    }
}
