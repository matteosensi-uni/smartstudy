package com.smartstudy.controller;

import com.smartstudy.businessLogic.AuthenticationService;
import com.smartstudy.businessLogic.LibraryAccessService;
import com.smartstudy.domainModel.Library;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.exceptions.DomainViolationException;

public class LibraryAccessController {
    private final LibraryAccessService libraryAccessService;

    public LibraryAccessController(LibraryAccessService libraryAccessService) {
        this.libraryAccessService = libraryAccessService;
    }

    public String handleAccess(long userId, long libraryId){
        try{
            libraryAccessService.toggleUserPresence(userId, libraryId);
            return null;
        }catch (BusinessViolationException | DataAccessException | DomainViolationException e){
            return e.getMessage();
        }
    }

    public boolean isStudentPresent(long studentId){
        return libraryAccessService.isStudentPresent(studentId);
    }
}
