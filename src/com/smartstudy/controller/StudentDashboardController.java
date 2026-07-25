package com.smartstudy.controller;

import DTO.AbandonmentReportDTO;
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
    public Seat scanSeat(Student user, String qrCode){
        return reservationService.scanSeat(qrCode, user.getId());
    }

    public Reservation reserveSeat(Student user, long seatId){
        return reservationService.createReservation(user.getId(), seatId);
    }

    public void closeReservation(Student user, Reservation reservation){
        reservationService.closeReservation(reservation.getId(), user.getId());
    }

    public Reservation createLeave(Student user, Reservation reservation){
        return temporaryLeaveService.createTemporaryLeave(reservation.getId(), user.getId());
    }

    public List<AbandonmentReportDTO> getAllReports(Student user){
        ArrayList<AbandonmentReport> reports = reportService.getOpenReportsByStudent(user.getId());
        ArrayList<AbandonmentReportDTO> dtos = new ArrayList<>();
        for (AbandonmentReport report : reports) {
            Reservation res = reservationService.getReservationByReport(report.getId());
            dtos.add(new AbandonmentReportDTO(AbandonmentReport.copy(report), res.getSeat().getId(), res.getSeat().getStudyArea().getName(), res.getSeat().getStudyArea().getLibrary().getName()));
        }
        return dtos;
    }

    public List<Reservation> getAllReservations(Student user){
        return reservationService.getReservationHistory(user.getId());
    }

    public void createAbandonmentReport(Student user, String description, long seatId) {
        reportService.createReport(user.getId(), description, seatId);
    }
}
