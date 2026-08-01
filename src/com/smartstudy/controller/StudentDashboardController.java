package com.smartstudy.controller;

import com.smartstudy.dto.AbandonmentReportDTO;
import com.smartstudy.businessLogic.ReportService;
import com.smartstudy.businessLogic.ReservationService;
import com.smartstudy.businessLogic.TemporaryLeaveService;
import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.Seat;
import com.smartstudy.domainModel.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardController {
    private final ReservationService reservationService;
    private final ReportService reportService;
    private final TemporaryLeaveService temporaryLeaveService;

    public StudentDashboardController(ReservationService reservationService, ReportService reportService, TemporaryLeaveService temporaryLeaveService) {
        this.reservationService = reservationService;
        this.reportService = reportService;
        this.temporaryLeaveService = temporaryLeaveService;
    }
    public ControllerResult<Seat> scanSeat(Student user, String qrCode){
        try {
            return ControllerResult.success(reservationService.scanSeat(qrCode, user.getId()));
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Reservation> reserveSeat(Student user, long seatId){
        try{
            return ControllerResult.success(reservationService.createReservation(user.getId(), seatId));
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }

    }

    public ControllerResult<Void> closeReservation(Student user, Reservation reservation){
        try{
            reservationService.closeReservation(reservation.getId(), user.getId());
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Reservation> createLeave(Student user, Reservation reservation){
        try {
            return ControllerResult.success(temporaryLeaveService.createTemporaryLeave(reservation.getId(), user.getId()));
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<List<AbandonmentReportDTO>> getAllReports(Student user){
        try {
            ArrayList<AbandonmentReport> reports = reportService.getOpenReportsByStudent(user.getId());
            ArrayList<AbandonmentReportDTO> dtos = new ArrayList<>();
            for (AbandonmentReport report : reports) {
                Reservation res = reservationService.getReservationByReport(report.getId());
                dtos.add(new AbandonmentReportDTO(AbandonmentReport.copy(report), res.getSeat().getId(), res.getSeat().getStudyArea().getName(), res.getSeat().getStudyArea().getLibrary().getName()));
            }
            return ControllerResult.success(dtos);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<List<Reservation>> getAllReservations(Student user){
        try{
            return ControllerResult.success(reservationService.getReservationHistory(user.getId()));
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }

    }

    public ControllerResult<Void> createAbandonmentReport(Student user, String description, long seatId) {
        try {
            reportService.createReport(user.getId(), description, seatId);
            return ControllerResult.success(null, "Segnalazione creata correttamente");
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }

    }
}
