package com.smartstudy.orm;

import com.smartstudy.domainModel.AccessSession;
import com.smartstudy.domainModel.Library;
import com.smartstudy.domainModel.Student;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.utils.TimeUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AccessSessionDAO extends BaseDAO{
    public static final String tableName = "access_session";
    public static final String pkName = "id_access";

    private final AdminDAO adminDAO;

    private final String AGGREGATE_QUERY = """
        select access_session.*,
                library.*,
                app_user.name as student_name,
                app_user.surname,
                app_user.password,
                app_user.email,
                student.*
        FROM access_session
        LEFT JOIN library ON library.id_library = access_session.id_library
        LEFT JOIN app_user ON app_user.user_id = access_session.student_id
        LEFT JOIN student ON app_user.user_id = student.user_id
    """;

    public AccessSessionDAO(Connection conn, AdminDAO adminDAO) { super(conn);
        this.adminDAO = adminDAO;
    }

    public Optional<AccessSession> getActiveAccessSessionByStudent(long studentId)  {
        try(PreparedStatement ps = conn.prepareStatement(AGGREGATE_QUERY + """
                WHERE student_id = ? AND exit_time IS NULL
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(createAccessSessionFromResultSet(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la sessione di accesso dello studente", e);
        }
    }

    public boolean hasActiveAccessSessionByStudent(long studentId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT 1 FROM access_session
                WHERE student_id = ? AND exit_time IS NULL
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la sessione di accesso dello studente", e);
        }
    }

    private AccessSession createAccessSessionFromResultSet(ResultSet rs) throws SQLException {
        Library library = Library.valueOf(
                rs.getLong("id_library"),
                rs.getString("name"),
                rs.getTime("opening_time").toLocalTime(),
                rs.getTime("closing_time").toLocalTime(),
                rs.getString("street"),
                rs.getString("number"),
                rs.getString("city"),
                adminDAO.getAdminsByLibraryId(rs.getLong("id_library"))
        );
        Student student = Student.valueOf(rs.getLong("student_id"),
                rs.getString("student_name"),
                rs.getString("surname"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getBoolean("card_active"));
        return AccessSession.valueOf(
                rs.getLong("id_access"),
                TimeUtils.getLocalTime(rs.getTimestamp("entry_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("exit_time")),
                library,
                student
        );
    }

    public AccessSession insert(AccessSession accessSession)  {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("entry_time", accessSession.getEntryTime());
            if (accessSession.getExitTime() != null) {
                values.put("exit_time", accessSession.getExitTime());
            }
            values.put("id_library", accessSession.getLibrary().getId());
            values.put("student_id", accessSession.getStudent().getId());
            Long id = DAOUtils.insert(conn, values, tableName);
            if(id == null) {
                throw new DataAccessException("Errore nell'inserimento del dato nel DB");
            }
            return AccessSession.valueOf(id, accessSession.getEntryTime(), accessSession.getExitTime(), accessSession.getLibrary(), accessSession.getStudent());
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile inserire la sessione di accesso nel DB", e);
        }
    }

    public void update(AccessSession accessSession) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            if (accessSession.getExitTime() != null) {
                values.put("exit_time", accessSession.getExitTime());
            } else {
                throw new IllegalArgumentException("AccessSession update list vuota");
            }
            DAOUtils.update(conn, values, tableName, pkName, accessSession.getId());
        }catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile modificare la sessione di accesso nel DB", e);
        }
    }
}
