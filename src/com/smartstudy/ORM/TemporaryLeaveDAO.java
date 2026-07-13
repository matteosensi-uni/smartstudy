package com.smartstudy.ORM;

import com.smartstudy.domainModel.TemporaryLeave;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.utils.TimeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TemporaryLeaveDAO extends BaseDAO implements Insertable<TemporaryLeave>{
    public static final String tableName = "temporary_leave";
    public static final String pkName = "id_leave";

    public TemporaryLeaveDAO(Connection conn) {
        super(conn);
    }

    public int countTemporaryLeavesByReservation(long reservationId) {
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
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le temporary leaves", e);
        }
    }

    public boolean hasActiveTemporaryLeave(long reservationId) {
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
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la temporary leave", e);
        }
    }

    public TemporaryLeave getTemporaryLeaveById(long temporaryLeaveId) {
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
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le temporary leaves", e);
        }
    }

    public ArrayList<TemporaryLeave> getTemporaryLeavesByReservation(long reservationId) {
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
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le temporary leaves", e);
        }
    }

    public ArrayList<TemporaryLeave> getExpiredTemporaryLeaves() {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT temporary_leave.*
                FROM temporary_leave  LEFT JOIN reservation ON  temporary_leave.id_reservation = reservation.id_reservation
                WHERE expected_end_time < now() AND reservation.status = 'TEMPORARILY_LEFT'
            """
            )){
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<TemporaryLeave> res = new ArrayList<>();
                while(rs.next())
                    res.add(createTemporaryLeaveFromResultSet(rs));
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le temporary leaves", e);
        }
    }

    private TemporaryLeave createTemporaryLeaveFromResultSet(ResultSet rs) throws SQLException {
        return TemporaryLeave.valueOf(
                rs.getLong("id_leave"),
                TimeUtils.getLocalTime(rs.getTimestamp("start_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("expected_end_time")),
                rs.getLong("id_reservation")
                );
    }

    @Override
    public TemporaryLeave insert(TemporaryLeave temporaryLeave) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("start_time", temporaryLeave.getStartTime());
            values.put("expected_end_time", temporaryLeave.getExpectedEndTime());
            values.put("id_reservation", temporaryLeave.getReservationId());
            Long id = DAOUtils.insert(conn, values, tableName);
            if(id == null) {
                throw new DataAccessException("Errore nell'inserimento del dato nel DB");
            }
            return TemporaryLeave.valueOf(id, temporaryLeave.getStartTime(), temporaryLeave.getExpectedEndTime(), temporaryLeave.getReservationId());
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile modificare la temporary leave", e);
        }
    }
}
