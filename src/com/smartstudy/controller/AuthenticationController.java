package com.smartstudy.controller;

import DTO.StudentSession;
import com.smartstudy.businessLogic.AuthenticationService;
import com.smartstudy.domainModel.Student;
import com.smartstudy.domainModel.User;

public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final LibraryAccessController libraryAccessController;

    public AuthenticationController(AuthenticationService authenticationService, LibraryAccessController libraryAccessController) {
        this.authenticationService = authenticationService;
        this.libraryAccessController = libraryAccessController;
    }

    public User handleLogin(String userID, String password) {
        return authenticationService.authenticateUser(userID, password);
    }

    public StudentSession createStudentSession(Student user)
    {
        return StudentSession.start(user, libraryAccessController.isStudentPresent(user.getId()));
    }
}
