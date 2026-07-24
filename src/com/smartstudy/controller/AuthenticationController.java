package com.smartstudy.controller;

import com.smartstudy.businessLogic.AuthenticationService;
import com.smartstudy.domainModel.User;

import java.util.Optional;

public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public Optional<User> handleLogin(long userID, String password) {
        return Optional.ofNullable(authenticationService.authenticateUser(userID, password));
    }
}
