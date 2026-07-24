package com.smartstudy.controller;

import com.smartstudy.businessLogic.ReportService;
import com.smartstudy.businessLogic.ReservationService;
import com.smartstudy.businessLogic.TemporaryLeaveService;
import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.Seat;
import com.smartstudy.domainModel.Student;

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
    public Seat scanSeat(Student user, String qrCode){
        return reservationService.scanSeat(qrCode, user.getId());
    }

    public void reportSeat(Student user, String description, long seatId){
        reportService.createReport(user.getId(), description, seatId);
    }

    public void reserveSeat(Student user, long seatId){
        reservationService.createReservation(user.getId(), seatId);
    }

    public void closeReservation(Student user, long reservationId){
        reservationService.closeReservation(reservationId, user.getId());
    }

    public void createLeave(Student user, long reservationId){
        temporaryLeaveService.createTemporaryLeave(user.getId(), reservationId);
    }

    public List<AbandonmentReport> getAllReports(Student user){
        return reportService.getOpenReportsByStudent(user.getId());
    }

    public List<Reservation> getAllReservations(Student user){
        return reservationService.getReservationHistory(user.getId());
    }
}
