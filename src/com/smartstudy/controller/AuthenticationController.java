package com.smartstudy.controller;

import com.smartstudy.businessLogic.AuthenticationService;
import com.smartstudy.domainModel.User;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.exceptions.DomainViolationException;

import java.util.Optional;

public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public Optional<User> handleLogin(long userID, String password) {
        Optional<User> res;
        try{
            res = Optional.of(authenticationService.authenticateUser(userID, password));
        }catch (DomainViolationException e){
            //out errore
            res = Optional.empty();
        }catch (DataAccessException e){
            //out errore
            res = Optional.empty();
        }catch (BusinessViolationException e){
            //out errore
            res = Optional.empty();
        }
        return res;
    }
}
