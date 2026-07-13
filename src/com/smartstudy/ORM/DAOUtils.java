package com.smartstudy.ORM;

import com.smartstudy.utils.SQLUtils;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

final class DAOUtils {
    private DAOUtils() {}
    static void update(Connection conn, Map<String, Object> values, String tableName, String pkName, long id) throws SQLException {
        Map<String, Object> orderedValues = new LinkedHashMap<>(values);
        try {
            String sql = SQLUtils.buildUpdateString(tableName, pkName, orderedValues);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int index = 1;
                for (Map.Entry<String, Object> entry : orderedValues.entrySet()) {
                    ps.setObject(index++, entry.getValue());
                }
                ps.setLong(index, id);
                ps.executeUpdate();

            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
    static Long insert(Connection conn, Map<String, Object> values, String tableName) throws SQLException {
        try {
            Map<String, Object> orderedValues = new LinkedHashMap<>(values);
            String sql = SQLUtils.buildInsertString(tableName, orderedValues);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                int index = 1;
                for (Map.Entry<String, Object> entry : orderedValues.entrySet()) {
                    ps.setObject(index++, entry.getValue());
                }
                ps.executeUpdate();
                try(ResultSet rs = ps.getGeneratedKeys()){
                    if(rs.next()){
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return null;
    }
}
