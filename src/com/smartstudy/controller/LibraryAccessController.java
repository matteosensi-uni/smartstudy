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

    public void handleAccess(long userId, long libraryId){
        try{
            libraryAccessService.toggleUserPresence(userId, libraryId);
        }catch (BusinessViolationException e){
            //stampa errore
        }catch (DataAccessException e){
            //stampa errore
        }catch (DomainViolationException e){
            //stampa errore
        }
    }
}
