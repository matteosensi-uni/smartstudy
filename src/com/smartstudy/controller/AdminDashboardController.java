package com.smartstudy.controller;
import com.smartstudy.DTO.AbandonmentReportDTO;
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
    public ControllerResult<List<AbandonmentReport>> getAbandonmentReportHandled(Admin user) {
        try{
            return ControllerResult.success(reportService.getClosedReportsByAdmin(user.getId()));
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<List<AbandonmentReportDTO>> getPendingAbandonmentReports(Admin user) {
        try {
            ArrayList<AbandonmentReport> reports = reportService.getReportsInChargeByAdmin(user.getId());
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

    public ControllerResult<List<AbandonmentReportDTO>> getOpenAbandonmentReports(Admin user) {
        try{
        ArrayList<AbandonmentReport> reports = reportService.getOpenReportsByAdmin(user.getId());
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

    public ControllerResult<Void> directConfirmReport(Admin user, AbandonmentReport report){
        try{
            reportService.takeInCharge(user.getId(), report.getId());
            reportService.confirm(user.getId(), report.getId());
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> directRejectReport(Admin user, AbandonmentReport report){
        try{
            reportService.takeInCharge(user.getId(), report.getId());
            reportService.reject(user.getId(), report.getId());
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> confirmReport(Admin user, AbandonmentReport report){
        try {
            reportService.confirm(user.getId(), report.getId());
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> rejectReport(Admin user, AbandonmentReport report){
        try {
            reportService.reject(user.getId(), report.getId());
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> takeInChargeReport(Admin user, AbandonmentReport report){
        try {
            reportService.takeInCharge(user.getId(), report.getId());
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<List<StudyArea>> getAllStudyAreas(Admin user){
        try {
            return ControllerResult.success(libraryConfigService.getLibraryStudyAreasByAdmin(user.getId()));
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> updateStudyAreaName(Admin user, String newName, StudyArea studyArea){
        try {
            libraryConfigService.updateStudyAreaName(studyArea.getId(), user.getId(), newName);
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> updateStudyAreaType(Admin user, String type, StudyArea studyArea){
        try {
            libraryConfigService.updateStudyAreaType(studyArea.getId(), user.getId(), type);
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> updateStudyAreaPolicy(Admin user, StudyArea studyArea, String policyName){
        try {
            libraryConfigService.updateStudyAreaPolicy(studyArea.getId(), user.getId(), policyName);
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<List<Seat>> getAllSeats(Admin user){
        try {
            return ControllerResult.success(libraryConfigService.getLibrarySeatsByAdmin(user.getId()));
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> updateSeatStatus(Admin user, Seat seat, String status){
        try {
            libraryConfigService.updateSeatStatus(seat.getId(), user.getId(), status);
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }

    public ControllerResult<Void> updateSeatType(Admin user, Seat seat, String type){
        try {
            libraryConfigService.updateSeatType(seat.getId(), user.getId(), type);
            return ControllerResult.success(null);
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }

    }

    public ControllerResult<List<String>> getAllTimePolicies(){
        try{
            return ControllerResult.success(libraryConfigService.getAllPolicies());
        }catch (Exception e){
            return ControllerResult.failure(e.getMessage());
        }
    }
}
