package com.smartstudy.ORM;

import com.smartstudy.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO extends BaseDAO{
    public static final String tableName = "app_user";
    public static final String pkName = "user_id";
    public UserDAO(Connection conn) {
        super(conn);
    }

    public boolean credentialsValid(long userId, String password){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM app_user
                WHERE app_user.user_id = ? AND app_user.password = ?
            """
            );
            ps.setLong(1, userId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
