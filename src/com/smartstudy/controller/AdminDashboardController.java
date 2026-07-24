package com.smartstudy.controller;
import com.smartstudy.businessLogic.LibraryAccessService;
import com.smartstudy.businessLogic.LibraryConfigService;
import com.smartstudy.businessLogic.ReportService;
import com.smartstudy.domainModel.*;

import java.util.List;

public class AdminDashboardController {
    private final ReportService reportService;
    private final LibraryConfigService libraryConfigService;

    public AdminDashboardController(ReportService reportService, LibraryAccessService libraryAccessService, LibraryConfigService libraryConfigService) {
        this.reportService = reportService;
        this.libraryConfigService = libraryConfigService;
    }
    public List<AbandonmentReport> getAbandonmentReportHandled(Admin user) {
        return reportService.getClosedReportsByAdmin(user.getId());
    }

    public List<AbandonmentReport> getPendingAbandonmentReports(Admin user) {
        return reportService.getReportsInChargeByAdmin(user.getId());
    }

    public List<AbandonmentReport> getOpenAbandonmentReports(Admin user) {
        return reportService.getOpenReportsByAdmin(user.getId());
    }

    public void directConfirmReport(Admin user, long reportId){
        reportService.takeInCharge(user.getId(), reportId);
        reportService.confirm(user.getId(), reportId);
    }

    public void directRejectReport(Admin user, long reportId){
        reportService.takeInCharge(user.getId(), reportId);
        reportService.reject(user.getId(), reportId);
    }

    public void confirmReport(Admin user, long reportId){
        reportService.confirm(reportId, user.getId());
    }

    public void rejectReport(Admin user, long reportId){
        reportService.reject(reportId, user.getId());
    }

    public void takeInChargeReport(Admin user, long reportId){
        reportService.takeInCharge(reportId, user.getId());
    }

    public List<StudyArea> getAllStudyAreas(Admin user){
        return libraryConfigService.getLibraryStudyAreasByAdmin(user.getId());
    }

    public void updateStudyAreaName(Admin user, String newName, long studyAreaId){
        libraryConfigService.updateStudyAreaName(studyAreaId, user.getId(), newName);
    }

    public void updateStudyAreaType(Admin user, String type, long studyAreaId){
        libraryConfigService.updateStudyAreaType(studyAreaId, user.getId(), type);
    }

    public void updateStudyAreaPolicy(Admin user, long studyAreaId, String policyName){
        libraryConfigService.updateStudyAreaPolicy(studyAreaId, user.getId(), policyName);
    }

    public List<Seat> getAllSeats(Admin user){
        return libraryConfigService.getLibrarySeatsByAdmin(user.getId());
    }

    public void updateSeatStatus(Admin user, long seatId, String status){
        libraryConfigService.updateSeatStatus(seatId, user.getId(), status);
    }

    public void updateSeatType(Admin user, long seatId, String type){
        libraryConfigService.updateSeatType(seatId, user.getId(), type);
    }

    public List<TimePolicy> getAllTimePolicies(){
        return libraryConfigService.getAllPolicies();
    }
}
