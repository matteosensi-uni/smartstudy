package com.smartstudy.ORM;

import com.smartstudy.DomainModel.TemporaryLeave;
import com.smartstudy.utils.TimeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class TemporaryLeaveDAO extends BaseDAO implements Updatable, Insertable{
    public static final String tableName = "temporary_leave";
    public static final String pkName = "id_leave";

    public TemporaryLeaveDAO(Connection conn) {
        super(conn);
    }

    public int countTemporaryLeavesByReservation(long reservationId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT count(id_seat) AS total
                FROM temporary_leave LEFT JOIN reservation ON temporary_leave.id_reservation = reservation.id_reservation
                WHERE reservation.id_reservation = ?
                AND (reservation.status = 'ACTIVE' OR reservation.status = 'TEMPORARILY_LEFT')
            """
            )){
            ps.setLong(1, reservationId);
            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasValidTemporaryLeave(long reservationId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM temporary_leave LEFT JOIN reservation ON temporary_leave.id_reservation = reservation.id_reservation
                WHERE reservation.id_reservation = ?
                AND reservation.status = 'TEMPORARILY_LEFT'
            """
            )){
            ps.setLong(1, reservationId);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TemporaryLeave getTemporaryLeaveById(long temporaryLeaveId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM temporary_leave WHERE id_leave = ?
            """
            )){
            ps.setLong(1, temporaryLeaveId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createTemporaryLeaveFromResultSet(rs);
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<TemporaryLeave> getTemporaryLeavesByReservation(long reservationId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT temporary_leave.*
                FROM temporary_leave LEFT JOIN reservation ON temporary_leave.id_reservation = reservation.id_reservation
                WHERE reservation.id_reservation = ?
                AND (reservation.status = 'ACTIVE' OR reservation.status = 'TEMPORARILY_LEFT')
            """
            )){
            ps.setLong(1, reservationId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<TemporaryLeave> res = new ArrayList<>();
                while(rs.next())
                    res.add(createTemporaryLeaveFromResultSet(rs));
                return res;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<TemporaryLeave> getExpiredTemporaryLeaves(long reservationId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM temporary_leave
                WHERE id_reservation = ?
                AND expected_end_time < now()
            """
            )){
            ps.setLong(1, reservationId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<TemporaryLeave> res = new ArrayList<>();
                while(rs.next())
                    res.add(createTemporaryLeaveFromResultSet(rs));
                return res;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TemporaryLeave createTemporaryLeaveFromResultSet(ResultSet rs) throws SQLException {
        return new TemporaryLeave(
                rs.getLong("id_leave"),
                TimeUtils.getLocalTime(rs.getTimestamp("start_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("expected_end_time")),
                rs.getLong("id_reservation")
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
