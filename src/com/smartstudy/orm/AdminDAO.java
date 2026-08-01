package com.smartstudy.orm;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AdminDAO extends BaseDAO{
    public static final String tableName = "admin";
    public static final String pkName = "user_id";
    public AdminDAO(Connection conn) { super(conn); }

    public Optional<Admin> getAdminById(long adminId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT app_user.*, admin.is_present, admin.id_library
                FROM admin LEFT JOIN app_user ON admin.user_id = app_user.user_id
                WHERE admin.user_id = ?
            """
            )){
            ps.setLong(1, adminId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(createAdminFromResultSet(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare l'admin", e);
        }
    }

    public ArrayList<Admin> getAdminsByLibraryId(long libraryId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT app_user.*, admin.is_present, admin.id_library
                FROM admin LEFT JOIN app_user ON admin.user_id = app_user.user_id
                WHERE admin.id_library = ?
            """
        )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<Admin> admins = new ArrayList<>();
                while (rs.next()){
                    admins.add(createAdminFromResultSet(rs));
                }
                return admins;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare l'admin", e);
        }
    }

    private Admin createAdminFromResultSet(ResultSet rs) throws SQLException {
        return Admin.valueOf(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getBoolean("is_present")
        );
    }

    public void update(Admin admin) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("is_present", admin.isPresent());
            DAOUtils.update(conn, values, tableName, pkName, admin.getId());
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile aggiornare il dato", e);
        }
    }
}
