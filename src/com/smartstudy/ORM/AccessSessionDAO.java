package com.smartstudy.ORM;

import com.smartstudy.domainModel.AccessSession;
import com.smartstudy.exceptions.DataAccessException;
import com.smartstudy.utils.TimeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccessSessionDAO extends BaseDAO{
    public static final String tableName = "access_session";
    public static final String pkName = "id_access";

    private final LibraryDAO libraryDAO;
    private final StudentDAO studentDAO;

    public AccessSessionDAO(Connection conn, LibraryDAO libraryDAO, StudentDAO studentDAO) { super(conn);
        this.libraryDAO = libraryDAO;
        this.studentDAO = studentDAO;
    }

    public AccessSession getActiveAccessSessionById(long sessionId)  {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM access_session
                WHERE id_access = ? AND exit_time IS NULL
            """
            )){
            ps.setLong(1, sessionId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createAccessSessionFromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le sessioni di accesso", e);
        }
    }

    public AccessSession getAccessSessionById(long sessionId)  {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM access_session
                WHERE id_access = ?
            """
            )){
            ps.setLong(1, sessionId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createAccessSessionFromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le sessioni di accesso", e);
        }
    }

    public AccessSession getActiveAccessSessionByStudent(long studentId)  {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM access_session
                WHERE student_id = ? AND exit_time IS NULL
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createAccessSessionFromResultSet(rs);
                }
                return null;
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
        return AccessSession.valueOf(
                rs.getLong("id_access"),
                TimeUtils.getLocalTime(rs.getTimestamp("entry_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("exit_time")),
                libraryDAO.getLibraryById(rs.getLong("id_library")),
                studentDAO.getStudentById(rs.getLong("student_id"))
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
