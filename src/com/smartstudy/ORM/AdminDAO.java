package com.smartstudy.ORM;
import com.smartstudy.DomainModel.Admin;
import com.smartstudy.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class AdminDAO extends BaseDAO implements Updatable {
    public static final String tableName = "admin";
    public static final String pkName = "user_id";
    public AdminDAO(Connection conn) { super(conn); }

    public Admin getAdminById(long adminId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT app_user.*, admin.is_present, admin.id_library
                FROM admin LEFT JOIN app_user ON admin.user_id = app_user.user_id
                WHERE admin.user_id = ?
            """
            );
            ps.setLong(1, adminId);
            ResultSet rs = ps.executeQuery();
            return createAdminFromResultSet(rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(long adminId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM admin
                WHERE admin.user_id = ?
            """
            );
            ps.setLong(1, adminId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Admin createAdminFromResultSet(ResultSet rs) throws SQLException {
        if(rs.next()){
            return new Admin(
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getString("surname"),
                    rs.getString("email"),
                    rs.getBoolean("is_present"),
                    rs.getLong("id_library")
            );
        }
        return null;
    }

    @Override
    public void update(Map<String, Object> values, long id) {
        DAOUtils.update(conn, values, tableName, pkName, id);
    }
}
