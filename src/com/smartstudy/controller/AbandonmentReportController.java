package com.smartstudy.controller;

import com.smartstudy.businessLogic.ReportService;
import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Student;
import java.util.List;

public class AbandonmentReportController {

    private final ReportService reportService;

    public AbandonmentReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public List<AbandonmentReport> getReportsMadeByStudent(Student user) {
        return reportService.getOpenReportsByStudent(user.getId());
    }

    public List<AbandonmentReport> getPendingReportsByAdmin(Admin admin) {
        return reportService.getReportsInChargeByAdmin(admin.getId());
    }

    public void createAbandonmentReport(Student user, String description, long reservationId) {
        reportService.createReport(user.getId(), description, reservationId);
    }
}
