package com.smartstudy.ORM;
import com.smartstudy.DomainModel.AbandonmentReport;
import com.smartstudy.DomainModel.enums.ReportStatus;
import com.smartstudy.utils.TimeUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class AbandonmentReportDAO extends BaseDAO implements Updatable, Insertable{
    public static final String tableName = "abandonment_report";
    public static final String pkName = "id_report";


    public AbandonmentReportDAO(Connection conn) {
        super(conn);
    }

    public AbandonmentReport getReportById(long reportId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE id_report = ?
            """
            );
            ps.setLong(1, reportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return createReportFromResultSet(rs);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<AbandonmentReport> getOpenReportsByStudent(long reportId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE student_id = ?
                AND (status = 'OPENED' OR status = 'PENDING')
            """
            );
            ps.setLong(1, reportId);
            ResultSet rs = ps.executeQuery();
            ArrayList<AbandonmentReport> res = new ArrayList<>();
            while(rs.next()){
                res.add(createReportFromResultSet(rs));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<AbandonmentReport> getInProgressReportsByAdmin(long adminId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE admin_id = ?
                AND status = 'PENDING'
            """
            );
            ps.setLong(1, adminId);
            ResultSet rs = ps.executeQuery();
            ArrayList<AbandonmentReport> res = new ArrayList<>();
            while(rs.next()){
                res.add(createReportFromResultSet(rs));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<AbandonmentReport> getClosedReportsByAdmin(long adminId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM abandonment_report WHERE admin_id = ?
                AND (status = 'CONFIRMED' OR status = 'REJECTED')
            """
            );
            ps.setLong(1, adminId);
            ResultSet rs = ps.executeQuery();
            ArrayList<AbandonmentReport> res = new ArrayList<>();
            while(rs.next()){
                res.add(createReportFromResultSet(rs));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<AbandonmentReport> getReportsByLibrary(long libraryId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM
                abandonment_report LEFT JOIN reservation ON reservation.id_reservation = abandonment_report.id_reservation
                LEFT JOIN access_session ON reservation.access_id = access_session.id_access
                WHERE access_session.id_library = ?
                AND reservation.status = 'OPENED'
            """
            );
            ps.setLong(1, libraryId);
            ResultSet rs = ps.executeQuery();
            ArrayList<AbandonmentReport> res = new ArrayList<>();
            while(rs.next()){
                res.add(createReportFromResultSet(rs));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsOpenReportByReservation(long reservationId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM abandonment_report
                WHERE id_reservation = ?
                AND status = 'OPENED'
            """
            );
            ps.setLong(1, reservationId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AbandonmentReport createReportFromResultSet(ResultSet rs) throws SQLException {
        return new AbandonmentReport(
                rs.getLong("id_report"),
                TimeUtils.getLocalTime(rs.getTimestamp("created_at")),
                TimeUtils.getLocalTime(rs.getTimestamp("resolved_at")),
                ReportStatus.valueOf(rs.getString("status")),
                rs.getString("description"),
                rs.getLong("id_reservation"),
                rs.getLong("student_id"),
                rs.getLong("admin_id")
        );
    }

    @Override
    public void insert(Map<String, Object> values) {
        DAOUtils.insert(conn, values, tableName);
    }

    @Override
    public void update(Map<String, Object> values, long id) {
        DAOUtils.update(conn, values, tableName, pkName, id);
    }
}
