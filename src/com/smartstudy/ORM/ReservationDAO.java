package com.smartstudy.ORM;

import com.smartstudy.DomainModel.Reservation;
import com.smartstudy.DomainModel.Seat;
import com.smartstudy.DomainModel.enums.ReservationStatus;
import com.smartstudy.utils.TimeUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class ReservationDAO extends BaseDAO implements Updatable, Insertable{
    public static final String tableName = "reservation";
    public static final String pkName = "id_reservation";
    public ReservationDAO(Connection conn) {
        super(conn);
    }

    public Reservation getReservationById(long reservationId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM reservation
                WHERE id_reservation = ?
            """
            );
            ps.setLong(1, reservationId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return createReservationFromResultSet(rs);
            else return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existReservationBySeat(long idSeat){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM reservation
                WHERE reservation.id_seat = ? AND (reservation.status = 'ACTIVE' OR reservation.status = 'TEMPORARILY_LEFT')
            """
            );
            ps.setLong(1, idSeat);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Reservation getActiveReservationBySeat(long idSeat){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT reservation.*
                FROM reservation
                WHERE reservation.id_seat = ? AND (reservation.status = 'ACTIVE' OR reservation.status = 'TEMPORARILY_LEFT')
            """
            );
            ps.setLong(1, idSeat);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return createReservationFromResultSet(rs);
            else return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Reservation getActiveReservationByAccessSession(long accessSessionId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT reservation.*
                FROM reservation LEFT JOIN access_session ON reservation.access_id = access_session.id_access
                WHERE reservation.access_id = ? AND (reservation.status = 'ACTIVE' OR reservation.status = 'TEMPORARILY_LEFT')
            """
            );
            ps.setLong(1, accessSessionId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return createReservationFromResultSet(rs);
            else return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Reservation> getReservationsByUser(long userId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT reservation.* FROM
                (reservation LEFT JOIN access_session ON reservation.access_id = access_session.id_access)
                LEFT JOIN student ON access_session.user_id = student.user_id
                WHERE student.user_id = ? AND reservation.status = 'CLOSED'
            """
            );
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Reservation> res = new ArrayList<>();
            while(rs.next())
                res.add(createReservationFromResultSet(rs));
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {

        return new Reservation(
                rs.getLong("id_reservation"),
                TimeUtils.getLocalTime(rs.getTimestamp("start_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("end_time")),
                ReservationStatus.valueOf(rs.getString("status")),
                rs.getLong("id_seat"),
                rs.getLong("access_id")
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
