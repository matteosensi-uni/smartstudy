package com.smartstudy.ORM;

import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends BaseDAO{
    public UserDAO(Connection conn) {
        super(conn);
    }

    public boolean credentialsValid(long userId, String password) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM app_user
                WHERE app_user.user_id = ? AND app_user.password = ?
            """
            )){
            ps.setLong(1, userId);
            ps.setString(2, password);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile verificare le credenziali", e);
        }
    }
}
