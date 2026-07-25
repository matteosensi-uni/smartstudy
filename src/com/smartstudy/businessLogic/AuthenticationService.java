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
    public User authenticateUser(String userId, String password) {
        if(userId == null || userId.isBlank()){
            throw new BusinessViolationException("Lo userId non può essere vuoto");
        }
        if(password == null || password.isBlank()){
            throw new BusinessViolationException("La password non può essere vuota");
        }
        password = password.trim();
        userId = userId.trim();
        long userid;
        try{
            userid = Long.parseLong(userId);
        }catch(NumberFormatException e){
            throw new BusinessViolationException("Inserire un id valido");
        }
        if(!userDAO.credentialsValid(userid, password)){
            throw new BusinessViolationException("I dati inseriti non sono validi");
        }
        if(studentDAO.existsById(userid)){
            return studentDAO.getStudentById(userid);
        }else if(adminDAO.existsById(userid)){
            return adminDAO.getAdminById(userid);
        }
        throw new BusinessViolationException("Ruolo non valido");
    }
}
