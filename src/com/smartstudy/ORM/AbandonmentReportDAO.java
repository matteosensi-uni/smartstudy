package com.smartstudy.ORM;
import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.enums.ReportStatus;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.utils.TimeUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AbandonmentReportDAO extends BaseDAO implements Updatable<AbandonmentReport>, Insertable<AbandonmentReport>{
    public static final String tableName = "abandonment_report";
    public static final String pkName = "id_report";

    public AbandonmentReportDAO(Connection conn) {
        super(conn);
    }

    public AbandonmentReport getReportById(long reportId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE id_report = ?
            """
            )){
            ps.setLong(1, reportId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createReportFromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero della segnalazione", e);
        }
    }

    public ArrayList<AbandonmentReport> getOpenReportsByStudent(long reportId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE student_id = ?
                AND (status = 'OPENED' OR status = 'PENDING')
            """
            )){
            ps.setLong(1, reportId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<AbandonmentReport> res = new ArrayList<>();
                while(rs.next()){
                    res.add(createReportFromResultSet(rs));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero delle segnalazione aperte dello studente", e);
        }
    }

    public ArrayList<AbandonmentReport> getInProgressReportsByAdmin(long adminId)  {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE admin_id = ?
                AND status = 'PENDING'
            """
            )){
            ps.setLong(1, adminId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<AbandonmentReport> res = new ArrayList<>();
                while(rs.next()){
                    res.add(createReportFromResultSet(rs));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero delle prenotazioni prese in carico dall'admin", e);
        }
    }

    public ArrayList<AbandonmentReport> getClosedReportsByAdmin(long adminId)  {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE admin_id = ?
                AND (status = 'CONFIRMED' OR status = 'REJECTED')
            """
            )){
            ps.setLong(1, adminId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<AbandonmentReport> res = new ArrayList<>();
                while(rs.next()){
                    res.add(createReportFromResultSet(rs));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero delle segnalazione chiuse della biblioteca", e);
        }
    }

    public ArrayList<AbandonmentReport> getReportsByLibrary(long libraryId)  {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT abandonment_report.* FROM
                abandonment_report LEFT JOIN reservation ON reservation.id_reservation = abandonment_report.id_reservation
                LEFT JOIN access_session ON reservation.access_id = access_session.id_access
                WHERE access_session.id_library = ?
                AND abandonment_report.status = 'OPENED'
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<AbandonmentReport> res = new ArrayList<>();
                while(rs.next()){
                    res.add(createReportFromResultSet(rs));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero delle segnalazione della biblioteca", e);
        }
    }

    public boolean existsOpenReportByReservation(long reservationId)  {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM abandonment_report
                WHERE id_reservation = ?
                AND status = 'OPENED'
            """
            )){
            ps.setLong(1, reservationId);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero delle segnalazione aperte", e);
        }
    }

    private AbandonmentReport createReportFromResultSet(ResultSet rs) throws SQLException {
        return AbandonmentReport.valueOf(
                rs.getLong("id_report"),
                TimeUtils.getLocalTime(rs.getTimestamp("created_at")),
                TimeUtils.getLocalTime(rs.getTimestamp("resolved_at")),
                ReportStatus.valueOf(rs.getString("status")),
                rs.getString("description"),
                rs.getLong("id_reservation"),
                rs.getLong("student_id"),
                rs.getObject("admin_id", Long.class)
        );
    }

    @Override
    public AbandonmentReport insert(AbandonmentReport abandonmentReport) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("created_at", abandonmentReport.getCreatedAt());
            if(abandonmentReport.getResolvedAt() != null) {
                values.put("resolved_at", abandonmentReport.getResolvedAt());
            }
            values.put("status", abandonmentReport.getStatus().name());
            if(abandonmentReport.getDescription() != null && !abandonmentReport.getDescription().isEmpty()) {
                values.put("description", abandonmentReport.getDescription());
            }
            values.put("student_id", abandonmentReport.getStudentId());
            if(abandonmentReport.getAdminId() != null) {
                values.put("admin_id", abandonmentReport.getAdminId());
            }
            values.put("id_reservation", abandonmentReport.getReservationId());

            Long id = DAOUtils.insert(conn, values, tableName);
            if(id == null) {
                throw new DataAccessException("Errore nell'inserimento del dato nel DB");
            }
            return AbandonmentReport.valueOf(id, abandonmentReport.getCreatedAt(), abandonmentReport.getResolvedAt(), abandonmentReport.getStatus(), abandonmentReport.getDescription(), abandonmentReport.getReservationId(), abandonmentReport.getStudentId(), abandonmentReport.getAdminId());
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento del dato nel DB", e);
        }
    }

    @Override
    public void update(AbandonmentReport abandonmentReport) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("status", abandonmentReport.getStatus().name());
            if (abandonmentReport.getAdminId() != null) {
                values.put("admin_id", abandonmentReport.getAdminId());
            }
            values.put("resolved_at", abandonmentReport.getResolvedAt());
            DAOUtils.update(conn, values, tableName, pkName, abandonmentReport.getId());
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella modifica del dato nel DB", e);
        }
    }
}
