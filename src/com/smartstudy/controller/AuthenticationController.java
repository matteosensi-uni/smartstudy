package com.smartstudy.controller;

import com.smartstudy.DTO.StudentSession;
import com.smartstudy.businessLogic.AuthenticationService;
import com.smartstudy.businessLogic.ReservationService;
import com.smartstudy.domainModel.Student;
import com.smartstudy.domainModel.User;

public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final ReservationService reservationService;
    private final LibraryAccessController libraryAccessController;

    public AuthenticationController(AuthenticationService authenticationService, ReservationService reservationService, LibraryAccessController libraryAccessController) {
        this.authenticationService = authenticationService;
        this.reservationService = reservationService;
        this.libraryAccessController = libraryAccessController;
    }

    public ControllerResult handleLogin(long userID, String password) {
        try {
            User user = authenticationService.authenticateUser(userID, password);
            if (user.isAdmin()) {
                return ControllerResult.success(user, "admin");
            } else
                return ControllerResult.success(StudentSession.start((Student) user, libraryAccessController.isStudentPresent(user.getId()), reservationService.getActiveStudentReservation(user.getId())),"student");
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }
}
