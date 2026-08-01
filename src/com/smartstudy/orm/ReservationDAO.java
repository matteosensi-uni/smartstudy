package com.smartstudy.orm;

import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.enums.ReservationStatus;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.utils.TimeUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ReservationDAO extends BaseDAO{
    public static final String tableName = "reservation";
    public static final String pkName = "id_reservation";

    private final SeatDAO seatDAO;
    private final AccessSessionDAO accessSessionDAO;
    private final AbandonmentReportDAO abandonmentReportDAO;
    private final TemporaryLeaveDAO temporaryLeaveDAO;

    public ReservationDAO(Connection conn, SeatDAO seatDAO, AccessSessionDAO accessSessionDAO, AbandonmentReportDAO abandonmentReportDAO, TemporaryLeaveDAO temporaryLeaveDAO) {
        super(conn);
        this.seatDAO = seatDAO;
        this.accessSessionDAO = accessSessionDAO;
        this.abandonmentReportDAO = abandonmentReportDAO;
        this.temporaryLeaveDAO = temporaryLeaveDAO;
    }

    public Optional<Reservation> getReservationById(long reservationId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM reservation
                WHERE id_reservation = ?
            """
            )){
            ps.setLong(1, reservationId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    Reservation res = createReservationFromResultSet(rs);
                    return Optional.of(res);
                }
                else return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la prenotazione", e);
        }
    }

    public Optional<Reservation> getReservationByReport(long reportId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT reservation.*
                FROM reservation LEFT JOIN abandonment_report ON reservation.id_reservation = abandonment_report.id_reservation
                WHERE id_report = ?
            """
        )){
            ps.setLong(1, reportId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    Reservation res = createReservationFromResultSet(rs);
                    return Optional.of(res);
                }
                else return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la prenotazione", e);
        }
    }

    public Optional<Reservation> getActiveReservationBySeat(long idSeat) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT reservation.*
                FROM reservation
                WHERE reservation.id_seat = ? AND (reservation.status = 'ACTIVE' OR reservation.status = 'TEMPORARILY_LEFT')
            """
            )){
            ps.setLong(1, idSeat);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    Reservation res = createReservationFromResultSet(rs);
                    return Optional.of(res);
                }
                else return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la prenotazione del posto", e);
        }
    }

    public Optional<Reservation> getActiveReservationByStudent(long studentId) {
        try (PreparedStatement ps = conn.prepareStatement("""
                SELECT reservation.* FROM
                (reservation LEFT JOIN access_session ON reservation.access_id = access_session.id_access)
                LEFT JOIN student ON access_session.student_id = student.user_id
                WHERE student.user_id = ? AND (reservation.status = 'ACTIVE' OR reservation.status = 'TEMPORARILY_LEFT')
            """
        )) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Reservation res = createReservationFromResultSet(rs);
                    return Optional.of(res);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la prenotazione dello studente", e);
        }
    }

    public ArrayList<Reservation> getReservationsByStudent(long studentId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT reservation.* FROM
                (reservation LEFT JOIN access_session ON reservation.access_id = access_session.id_access)
                LEFT JOIN student ON access_session.student_id = student.user_id
                WHERE student.user_id = ? AND reservation.status = 'CLOSED'
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<Reservation> res = new ArrayList<>();
                while(rs.next())
                    res.add(createReservationFromResultSet(rs));
                return res;
            }
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le prenotazioni dello studente", e);
        }
    }

    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        return Reservation.valueOf(
                rs.getLong("id_reservation"),
                TimeUtils.getLocalTime(rs.getTimestamp("start_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("end_time")),
                ReservationStatus.valueOf(rs.getString("status")),
                temporaryLeaveDAO.getTemporaryLeavesByReservation(rs.getLong("id_reservation")),
                accessSessionDAO.getAccessSessionById(rs.getLong("access_id")).orElse(null),
                seatDAO.getSeatById(rs.getLong("id_seat")).orElse(null),
                abandonmentReportDAO.getReportsByReservationId(rs.getLong("id_reservation"))
        );
    }

    public Reservation insert(Reservation reservation) {
        try {
            reservation.refreshState();
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("start_time", reservation.getStartTime());
            if (reservation.getEndTime() != null) {
                values.put("end_time", reservation.getEndTime());
            }
            values.put("status", reservation.getStatus().name());
            values.put("id_seat", reservation.getSeat().getId());
            values.put("access_id", reservation.getSession().getId());
            Long id = DAOUtils.insert(conn, values, tableName);
            if(id == null) {
                throw new DataAccessException("Errore nell'inserimento del dato nel DB");
            }
            return Reservation.valueOf(id, reservation.getStartTime(), reservation.getEndTime(), reservation.getStatus(), reservation.getTemporaryLeaves(), reservation.getSession(), reservation.getSeat(), reservation.getAbandonmentReports());
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile inserire la prenotazione nel DB", e);
        }
    }

    public void update(Reservation reservation) {
        try {
            reservation.refreshState();
            Map<String, Object> values = new LinkedHashMap<>();
            if (reservation.getEndTime() != null) {
                values.put("end_time", reservation.getEndTime());
            }
            values.put("status", reservation.getStatus().name());
            DAOUtils.update(conn, values, tableName, pkName, reservation.getId());
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile modificare la prenotazione nel DB", e);
        }
    }
}
