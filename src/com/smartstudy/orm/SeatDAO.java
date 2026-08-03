package com.smartstudy.orm;

import com.smartstudy.domainModel.*;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SeatDAO extends BaseDAO{
    public static final String tableName = "seat";
    public static final String pkName = "id_seat";

    private final AdminDAO adminDAO;

    private final String AGGREGATE_QUERY = """
            select seat.*,
                    library.*,
                    study_area.name as sa_name,
                    study_area.floor,
                    study_area.type as sa_type,
                    time_policy.name as tp_name,
                    time_policy.max_temporary_leave_min,
                    time_policy.max_temporary_leave_times,
                    time_policy.id_policy
            FROM seat
            LEFT JOIN study_area ON study_area.id_area = seat.id_area
            LEFT JOIN library ON library.id_library = study_area.id_library
            LEFT JOIN time_policy ON study_area.id_policy = time_policy.id_policy
    """;

    public SeatDAO(Connection conn, AdminDAO adminDAO) { super(conn);
        this.adminDAO = adminDAO;
    }

    public Optional<Seat> getSeatById(long seatId){
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE id_seat = ?
            """
            )){
            ps.setLong(1, seatId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return Optional.of(createSeatFromResultSet(rs));
                else return Optional.empty();
            }
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare il posto", e);
        }
    }

    public Optional<Seat> getSeatByQR(String qrCode){
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE qr_code = ?
            """
            )){
            ps.setString(1, qrCode);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return Optional.of(createSeatFromResultSet(rs));
                else return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare il posto", e);
        }
    }

    public ArrayList<Seat> getLibrarySeats(long libraryId){
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE library.id_library = ?
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<Seat> res = new ArrayList<>();
                Library library = null;
                while(rs.next()) {
                    if(library == null){
                        library = Library.valueOf(
                                rs.getLong("id_library"),
                                rs.getString("name"),
                                rs.getTime("opening_time").toLocalTime(),
                                rs.getTime("closing_time").toLocalTime(),
                                rs.getString("street"),
                                rs.getString("number"),
                                rs.getString("city"),
                                adminDAO.getAdminsByLibraryId(rs.getLong("id_library"))
                        );
                    }
                    res.add(createSeatFromResultSet(rs, library));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare i posti della libreria", e);
        }
    }

    private Seat createSeatFromResultSet(ResultSet rs) throws SQLException {
        Library lib = Library.valueOf(
                rs.getLong("id_library"),
                rs.getString("name"),
                rs.getTime("opening_time").toLocalTime(),
                rs.getTime("closing_time").toLocalTime(),
                rs.getString("street"),
                rs.getString("number"),
                rs.getString("city"),
                adminDAO.getAdminsByLibraryId(rs.getLong("id_library"))
        );
        return createSeatFromResultSet(rs, lib);
    }

    private Seat createSeatFromResultSet(ResultSet rs, Library lib) throws SQLException {
        TimePolicy timePolicy = TimePolicy.valueOf(rs.getLong("id_policy"), rs.getInt("max_temporary_leave_min"), rs.getInt("max_temporary_leave_times"), rs.getString("tp_name"));
        StudyArea studyArea = StudyArea.valueOf(
                rs.getLong("id_area"),
                rs.getString("sa_name"),
                rs.getInt("floor"),
                StudyAreaType.valueOf(rs.getString("sa_type")),
                timePolicy,
                lib
        );
        return Seat.valueOf(
                rs.getLong("id_seat"),
                rs.getString("qr_code"),
                SeatType.valueOf(rs.getString("type")),
                SeatStatus.valueOf(rs.getString("status")),
                studyArea
        );
    }

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
