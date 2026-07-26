package com.smartstudy.controller;

import com.smartstudy.businessLogic.LibraryAccessService;

public class LibraryAccessController {
    private final LibraryAccessService libraryAccessService;

    public LibraryAccessController(LibraryAccessService libraryAccessService) {
        this.libraryAccessService = libraryAccessService;
    }

    public ControllerResult<Void> handleAccess(long userId, long libraryId){
        try{
            boolean res = libraryAccessService.toggleUserPresence(userId, libraryId);
            return ControllerResult.success(null,"L'utente è "+ (res? "entrato":"uscito") + " correttamente");
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    boolean isStudentPresent(long studentId){
        return libraryAccessService.isStudentPresent(studentId);
    }
}
