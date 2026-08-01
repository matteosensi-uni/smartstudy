package com.smartstudy.orm;

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

public class TemporaryLeaveDAO extends BaseDAO{
    public static final String tableName = "temporary_leave";
    public static final String pkName = "id_leave";

    public TemporaryLeaveDAO(Connection conn) {
        super(conn);
    }

    public ArrayList<TemporaryLeave> getTemporaryLeavesByReservation(long reservationId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT temporary_leave.*
                FROM temporary_leave LEFT JOIN reservation ON temporary_leave.id_reservation = reservation.id_reservation
                WHERE reservation.id_reservation = ?
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


    private TemporaryLeave createTemporaryLeaveFromResultSet(ResultSet rs) throws SQLException {
        return TemporaryLeave.valueOf(
                rs.getLong("id_leave"),
                TimeUtils.getLocalTime(rs.getTimestamp("start_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("expected_end_time"))
                );
    }

    public TemporaryLeave insert(TemporaryLeave temporaryLeave, long reservationId) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("start_time", temporaryLeave.getStartTime());
            values.put("expected_end_time", temporaryLeave.getExpectedEndTime());
            values.put("id_reservation", reservationId);
            Long id = DAOUtils.insert(conn, values, tableName);
            if(id == null) {
                throw new DataAccessException("Errore nell'inserimento del dato nel DB");
            }
            return TemporaryLeave.valueOf(id, temporaryLeave.getStartTime(), temporaryLeave.getExpectedEndTime());
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile modificare la temporary leave", e);
        }
    }
}
