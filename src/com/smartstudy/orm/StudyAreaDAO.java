package com.smartstudy.orm;

import com.smartstudy.domainModel.Library;
import com.smartstudy.domainModel.StudyArea;
import com.smartstudy.domainModel.TimePolicy;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class StudyAreaDAO extends BaseDAO{
    public static final String tableName = "study_area";
    public static final String pkName = "id_area";

    private final String AGGREGATE_QUERY = """
            select library.*,
                    study_area.id_area,
                    study_area.name as sa_name,
                    study_area.floor,
                    study_area.type as sa_type,
                    time_policy.name as tp_name,
                    time_policy.max_temporary_leave_min,
                    time_policy.max_temporary_leave_times,
                    time_policy.id_policy
            FROM study_area
            LEFT JOIN library ON library.id_library = study_area.id_library
            LEFT JOIN time_policy ON study_area.id_policy = time_policy.id_policy
    """;

    private final AdminDAO adminDAO;

    public StudyAreaDAO(Connection conn, AdminDAO adminDAO) {
        super(conn);
        this.adminDAO = adminDAO;
    }
    public Optional<StudyArea> getStudyAreaById(long studyAreaId){
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE study_area.id_area = ?
            """
            )){
            ps.setLong(1, studyAreaId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return Optional.of(createStudyAreaFromResultSet(rs));
                else return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare l'area studio", e);
        }
    }

    public ArrayList<StudyArea> getLibraryStudyAreas(long libraryId){
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE study_area.id_library = ?
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()) {
                ArrayList<StudyArea> res = new ArrayList<>();
                Library library = null;
                while (rs.next()){
                    if (library == null) {
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
                    res.add(createStudyAreaFromResultSet(rs, library));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le aree studio", e);
        }
    }

    private StudyArea createStudyAreaFromResultSet(ResultSet rs) throws SQLException {
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
        return createStudyAreaFromResultSet(rs, lib);
    }

    private StudyArea createStudyAreaFromResultSet(ResultSet rs, Library lib) throws SQLException {
        TimePolicy timePolicy = TimePolicy.valueOf(rs.getLong("id_policy"), rs.getInt("max_temporary_leave_min"), rs.getInt("max_temporary_leave_times"), rs.getString("tp_name"));
        return StudyArea.valueOf(
                rs.getLong("id_area"),
                rs.getString("sa_name"),
                rs.getInt("floor"),
                StudyAreaType.valueOf(rs.getString("sa_type")),
                timePolicy,
                lib
        );

    }

    public void update(StudyArea studyArea) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("name", studyArea.getName());
            values.put("type", studyArea.getType().name());
            values.put("id_policy", studyArea.getTimePolicy().getId());
            DAOUtils.update(conn, values, tableName, pkName, studyArea.getId());
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile aggriornare l'area studio", e);
        }
    }
}
