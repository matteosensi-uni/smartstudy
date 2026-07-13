package com.smartstudy.ORM;

import com.smartstudy.DomainModel.Seat;
import com.smartstudy.DomainModel.enums.SeatStatus;
import com.smartstudy.DomainModel.enums.SeatType;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SeatDAO extends BaseDAO implements Updatable<Seat>{
    public static final String tableName = "seat";
    public static final String pkName = "id_seat";
    public SeatDAO(Connection conn) { super(conn); }

    public Seat getSeatById(long seatId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM seat
                WHERE id_seat = ?
            """
            )){
            ps.setLong(1, seatId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return createSeatFromResultSet(rs);
                else return null;
            }
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare il posto", e);
        }
    }

    public ArrayList<Seat> getStudyAreaSeats(long seatAreaId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM seat
                WHERE id_area = ?
            """
            )){
            ps.setLong(1, seatAreaId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<Seat> res = new ArrayList<>();
                while(rs.next())
                    res.add(createSeatFromResultSet(rs));
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare i posti dell'area", e);
        }
    }

    public Seat getSeatByQR(String qrCode){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM seat
                WHERE qr_code = ?
            """
            )){
            ps.setString(1, qrCode);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return createSeatFromResultSet(rs);
                else return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare il posto", e);
        }
    }

    public ArrayList<Seat> getLibrarySeats(long libraryId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT seat.*
                FROM (seat LEFT JOIN study_area ON seat.id_area = study_area.id_area)
                LEFT JOIN library ON study_area.id_library = library.id_library
                WHERE library.id_library = ?
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<Seat> res = new ArrayList<>();
                while(rs.next())
                    res.add(createSeatFromResultSet(rs));
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare i posti della libreria", e);
        }
    }

    public int countAvailableSeatsByLibrary(long libraryId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT count(id_seat) AS total
                FROM (seat LEFT JOIN study_area ON seat.id_area = study_area.id_area)
                LEFT JOIN library ON study_area.id_library = library.id_library
                WHERE library.id_library = ? AND seat.status = 'AVAILABLE'
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getInt("total");
            }
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare i posti", e);
        }
    }

    public int countBrokenSeatsByLibrary(long libraryId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT count(id_seat) AS total
                FROM (seat LEFT JOIN study_area ON seat.id_area = study_area.id_area)
                LEFT JOIN library ON study_area.id_library = library.id_library
                WHERE library.id_library = ? AND seat.status = 'BROKEN'
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare i posti", e);
        }
    }

    public int countOccupiedSeatsByLibrary(long libraryId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT count(id_seat) AS total
                FROM (seat LEFT JOIN study_area ON seat.id_area = study_area.id_area)
                LEFT JOIN library ON study_area.id_library = library.id_library
                WHERE library.id_library = ? AND seat.status = 'UNAVAILABLE'
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare il posti", e);
        }
    }

    private Seat createSeatFromResultSet(ResultSet rs) throws SQLException {
        return Seat.valueOf(
                rs.getLong("id_seat"),
                rs.getString("qr_code"),
                SeatType.valueOf(rs.getString("type")),
                SeatStatus.valueOf(rs.getString("status")),
                rs.getLong("id_area")
        );

    }

    @Override
    public void update(Seat seat) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("status", seat.getStatus().name());
            values.put("type", seat.getType().name());
            DAOUtils.update(conn, values, tableName, pkName, seat.getId());
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile modificare il posto", e);
        }
    }
}
