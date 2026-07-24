package com.smartstudy.controller;

import com.smartstudy.businessLogic.LibraryAccessService;

public class LibraryAccessController {
    private final LibraryAccessService libraryAccessService;

    public LibraryAccessController(LibraryAccessService libraryAccessService) {
        this.libraryAccessService = libraryAccessService;
    }

    public void handleAccess(long userId, long libraryId){
        libraryAccessService.toggleUserPresence(userId, libraryId);
    }

    public boolean isStudentPresent(long studentId){
        return libraryAccessService.isStudentPresent(studentId);
    }
}
