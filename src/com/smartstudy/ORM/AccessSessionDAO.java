package com.smartstudy.ORM;

import com.smartstudy.DomainModel.AccessSession;
import com.smartstudy.utils.TimeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccessSessionDAO extends BaseDAO implements Updatable<AccessSession>, Insertable<AccessSession>{
    public static final String tableName = "access_session";
    public static final String pkName = "id_access";

    public AccessSessionDAO(Connection conn) { super(conn); }

    public AccessSession getActiveAccessSessionById(long sessionId){
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AccessSession getActiveAccessSessionByStudent(long studentId){
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasActiveAccessSessionByStudent(long studentId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT 1 FROM access_session
                WHERE student_id = ? AND exit_time IS NULL
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AccessSession createAccessSessionFromResultSet(ResultSet rs) throws SQLException {
        return new AccessSession(
                rs.getLong("id_access"),
                TimeUtils.getLocalTime(rs.getTimestamp("entry_time")),
                TimeUtils.getLocalTime(rs.getTimestamp("exit_time")),
                rs.getLong("id_library"),
                rs.getLong("user_id")
        );
    }

    @Override
    public void insert(AccessSession accessSession) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("entry_time", accessSession.getEntryTime());
        if(accessSession.getExitTime() != null){
            values.put("exit_time", accessSession.getExitTime());
        }
        values.put("id_library", accessSession.getLibraryId());
        values.put("student_id", accessSession.getStudent_id());
        DAOUtils.insert(conn, values, tableName);
    }

    @Override
    public void update(AccessSession accessSession) {
        Map<String, Object> values = new LinkedHashMap<>();
        if(accessSession.getExitTime() != null){
            values.put("exit_time", accessSession.getExitTime());
        }
        DAOUtils.update(conn, values, tableName, pkName, accessSession.getId());
    }
}
