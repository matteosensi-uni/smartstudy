package com.smartstudy.orm;
import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Student;
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
import java.util.Optional;

public class AbandonmentReportDAO extends BaseDAO{
    public static final String tableName = "abandonment_report";
    public static final String pkName = "id_report";

    private final String AGGREGATE_QUERY = """
            select abandonment_report.*,
            		student_data.name as student_name,
            		student_data.surname as student_surname,
            		student_data.password AS student_pwd,
            		student_data.email as student_email,
            		student.card_active,
            		admin_data.name as admin_name,
            		admin_data.surname as admin_surname,
            		admin_data.password AS admin_pwd,
            		admin_data.email as admin_email,
            		admin.is_present
            from abandonment_report
            left join app_user as student_data on student_data.user_id = abandonment_report.student_id
            left join student on student.user_id = student_data.user_id
            left join app_user as admin_data on admin_data.user_id = abandonment_report.admin_id
            left join admin on admin.user_id = admin_data.user_id
            """;

    public AbandonmentReportDAO(Connection conn) {
        super(conn);
    }

    public Optional<AbandonmentReport> getReportById(long reportId){
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE id_report = ?
            """
            )){
            ps.setLong(1, reportId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(createReportFromResultSet(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero della segnalazione", e);
        }
    }

    public ArrayList<AbandonmentReport> getOpenReportsByStudent(long studentId) {
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE student_id = ?
            """
            )){
            ps.setLong(1, studentId);
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

    public ArrayList<AbandonmentReport> getReportsByReservationId(long reservationId) {
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE  id_reservation = ?
            """
        )){
            ps.setLong(1, reservationId);
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
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE admin_id = ?
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
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY +"""
                WHERE admin_id = ?
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
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                LEFT JOIN reservation ON reservation.id_reservation = abandonment_report.id_reservation
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

    private AbandonmentReport createReportFromResultSet(ResultSet rs) throws SQLException {
        Admin admin;
        Integer adminId = rs.getObject("admin_id", Integer.class);
        if(adminId == null){
            admin = null;
        }else{
            admin = Admin.valueOf(adminId, rs.getString("admin_name"), rs.getString("admin_surname"), rs.getString("admin_pwd"), rs.getString("admin_email"), rs.getBoolean("is_present"));
        }
        Student author = Student.valueOf(rs.getLong("student_id"), rs.getString("student_name"), rs.getString("student_surname"), rs.getString("student_pwd"), rs.getString("student_email"), rs.getBoolean("card_active"));
        return AbandonmentReport.valueOf(
                rs.getLong("id_report"),
                TimeUtils.getLocalTime(rs.getTimestamp("created_at")),
                TimeUtils.getLocalTime(rs.getTimestamp("resolved_at")),
                ReportStatus.valueOf(rs.getString("status")),
                rs.getString("description"),
                author,
                admin
        );
    }

    public AbandonmentReport insert(AbandonmentReport abandonmentReport, long reservationId)  {
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
            values.put("student_id", abandonmentReport.getAuthor().getId());
            if(abandonmentReport.getAdmin() != null) {
                values.put("admin_id", abandonmentReport.getAdmin().getId());
            }
            values.put("id_reservation", reservationId);

            Long id = DAOUtils.insert(conn, values, tableName);
            if(id == null) {
                throw new DataAccessException("Errore nell'inserimento del dato nel DB");
            }
            return AbandonmentReport.valueOf(id, abandonmentReport.getCreatedAt(), abandonmentReport.getResolvedAt(), abandonmentReport.getStatus(), abandonmentReport.getDescription(), abandonmentReport.getAuthor(), abandonmentReport.getAdmin());
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento del dato nel DB", e);
        }
    }

    public void update(AbandonmentReport abandonmentReport) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("status", abandonmentReport.getStatus().name());
            values.put("admin_id", abandonmentReport.getAdmin() != null ? abandonmentReport.getAdmin().getId() : null);
            values.put("resolved_at", abandonmentReport.getResolvedAt());
            DAOUtils.update(conn, values, tableName, pkName, abandonmentReport.getId());
        } catch (SQLException e) {
            throw new DataAccessException("Errore nella modifica del dato nel DB", e);
        }
    }
}
