package com.smartstudy.businessLogic;

import com.smartstudy.domainModel.User;
import com.smartstudy.ORM.AdminDAO;
import com.smartstudy.ORM.StudentDAO;
import com.smartstudy.ORM.UserDAO;
import com.smartstudy.exceptions.BusinessViolationException;

public class AuthenticationService {
    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final AdminDAO adminDAO;
    public AuthenticationService(UserDAO userDAO, StudentDAO studentDAO, AdminDAO adminDAO) {
        this.userDAO = userDAO;
        this.studentDAO = studentDAO;
        this.adminDAO = adminDAO;
    }
    public User authenticateUser(long userId, String password) {
        if(password == null || password.isBlank()){
            throw new BusinessViolationException("La password non può essere vuota");
        }
        password = password.trim();
        if(!userDAO.credentialsValid(userId, password)){
            throw new BusinessViolationException("I dati inseriti non sono validi");
        }
        if(studentDAO.existsById(userId)){
            return studentDAO.getStudentById(userId);
        }else if(adminDAO.existsById(userId)){
            return adminDAO.getAdminById(userId);
        }
        throw new BusinessViolationException("Ruolo non valido");
    }
}
