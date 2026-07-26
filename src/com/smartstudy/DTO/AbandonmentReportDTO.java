package com.smartstudy.DTO;

import com.smartstudy.domainModel.AbandonmentReport;

public class AbandonmentReportDTO {
    private final AbandonmentReport report;
    private final long seatId;
    private final String studyAreaName;
    private final String libraryName;

    public AbandonmentReportDTO(AbandonmentReport report, long seatId, String studyAreaName, String libraryName) {
        this.report = report;
        this.seatId = seatId;
        this.studyAreaName = studyAreaName;
        this.libraryName = libraryName;
    }

    public AbandonmentReport getReport() {
        return report;
    }
    public long getSeatId() {
        return seatId;
    }
    public String getStudyAreaName() {
        return studyAreaName;
    }
    public String getLibraryName() { return libraryName; }
}
