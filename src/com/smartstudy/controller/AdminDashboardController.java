package com.smartstudy.controller;
import DTO.AbandonmentReportDTO;
import com.smartstudy.businessLogic.LibraryConfigService;
import com.smartstudy.businessLogic.ReportService;
import com.smartstudy.businessLogic.ReservationService;
import com.smartstudy.domainModel.*;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardController {
    private final ReportService reportService;
    private final LibraryConfigService libraryConfigService;
    private final ReservationService reservationService;

    public AdminDashboardController(ReportService reportService, LibraryConfigService libraryConfigService, ReservationService reservationService) {
        this.reportService = reportService;
        this.libraryConfigService = libraryConfigService;
        this.reservationService = reservationService;
    }
    public List<AbandonmentReport> getAbandonmentReportHandled(Admin user) {
        return reportService.getClosedReportsByAdmin(user.getId());
    }

    public List<AbandonmentReportDTO> getPendingAbandonmentReports(Admin user) {
        ArrayList<AbandonmentReport> reports = reportService.getReportsInChargeByAdmin(user.getId());
        ArrayList<AbandonmentReportDTO> dtos = new ArrayList<>();
        for (AbandonmentReport report : reports) {
            Reservation res = reservationService.getReservationByReport(report.getId());
            dtos.add(new AbandonmentReportDTO(AbandonmentReport.copy(report), res.getSeat().getId(), res.getSeat().getStudyArea().getName(), res.getSeat().getStudyArea().getLibrary().getName()));
        }
        return dtos;
    }

    public List<AbandonmentReportDTO> getOpenAbandonmentReports(Admin user) {
        ArrayList<AbandonmentReport> reports = reportService.getOpenReportsByAdmin(user.getId());
        ArrayList<AbandonmentReportDTO> dtos = new ArrayList<>();
        for (AbandonmentReport report : reports) {
            Reservation res = reservationService.getReservationByReport(report.getId());
            dtos.add(new AbandonmentReportDTO(AbandonmentReport.copy(report), res.getSeat().getId(), res.getSeat().getStudyArea().getName(), res.getSeat().getStudyArea().getLibrary().getName()));
        }
        return dtos;
    }

    public void directConfirmReport(Admin user, AbandonmentReport report){
        reportService.takeInCharge(user.getId(), report.getId());
        reportService.confirm(user.getId(), report.getId());
    }

    public void directRejectReport(Admin user, AbandonmentReport report){
        reportService.takeInCharge(user.getId(), report.getId());
        reportService.reject(user.getId(), report.getId());
    }

    public void confirmReport(Admin user, AbandonmentReport report){
        reportService.confirm(user.getId(), report.getId());
    }

    public void rejectReport(Admin user, AbandonmentReport report){
        reportService.reject(user.getId(), report.getId());
    }

    public void takeInChargeReport(Admin user, AbandonmentReport report){
        reportService.takeInCharge(user.getId(), report.getId());
    }

    public List<StudyArea> getAllStudyAreas(Admin user){
        return libraryConfigService.getLibraryStudyAreasByAdmin(user.getId());
    }

    public void updateStudyAreaName(Admin user, String newName, StudyArea studyArea){
        libraryConfigService.updateStudyAreaName(studyArea.getId(), user.getId(), newName);
    }

    public void updateStudyAreaType(Admin user, String type, StudyArea studyArea){
        libraryConfigService.updateStudyAreaType(studyArea.getId(), user.getId(), type);
    }

    public void updateStudyAreaPolicy(Admin user, StudyArea studyArea, String policyName){
        libraryConfigService.updateStudyAreaPolicy(studyArea.getId(), user.getId(), policyName);
    }

    public List<Seat> getAllSeats(Admin user){
        return libraryConfigService.getLibrarySeatsByAdmin(user.getId());
    }

    public void updateSeatStatus(Admin user, Seat seat, String status){
        libraryConfigService.updateSeatStatus(seat.getId(), user.getId(), status);
    }

    public void updateSeatType(Admin user, Seat seat, String type){
        libraryConfigService.updateSeatType(seat.getId(), user.getId(), type);
    }

    public List<String> getAllTimePolicies(){
        return libraryConfigService.getAllPolicies();
    }
}
