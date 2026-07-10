package com.smartstudy.ORM;

import com.smartstudy.DomainModel.AccessSession;
import com.smartstudy.utils.TimeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class AccessSessionDAO extends BaseDAO implements Updatable, Insertable{
    public static final String tableName = "access_session";
    public static final String pkName = "id_access";

    public AccessSessionDAO(Connection conn) { super(conn); }

    public AccessSession getActiveAccessSessionById(long sessionId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM access_session
                WHERE id_access = ?
            """
            );
            ps.setLong(1, sessionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return createAccessSessionFromResultSet(rs);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AccessSession getActiveAccessSessionByUser(long userId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM access_session
                WHERE user_id = ? AND exit_time IS NOT NULL
            """
            );
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return createAccessSessionFromResultSet(rs);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasActiveAccessSessionByUser(long userId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT 1 FROM access_session
                WHERE user_id = ? AND exit_time IS NOT NULL
            """
            );
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
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
    public void insert(Map<String, Object> values) {
        DAOUtils.insert(conn, values, tableName);
    }

    @Override
    public void update(Map<String, Object> values, long id) {
        DAOUtils.update(conn, values, tableName, pkName, id);
    }
}
