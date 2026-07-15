package com.smartstudy.ORM;

import com.smartstudy.exceptions.DomainViolationException;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

final class DAOUtils {
    private DAOUtils() {}

    private static String buildUpdateString(String tableName, String pk_name, Map<String, Object> values){
        StringBuilder placeholders = new StringBuilder();
        if(values == null || values.isEmpty())
            throw new DomainViolationException("Mancano i parametri della update");
        for (String key : values.keySet()) {
            placeholders.append(key).append(" = ?, ");
        }
        placeholders.setLength(placeholders.length() - 2);
        return "UPDATE "
                + tableName
                + " SET "
                + placeholders
                + " WHERE "
                + pk_name + "= ?";
    }

    private static String buildInsertString(String tableName, Map<String, Object> values){
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        if(values == null || values.isEmpty())
            throw new DomainViolationException("Mancano i parametri della insert");
        for (String key : values.keySet()) {
            columns.append(key).append(", ");
            placeholders.append("?, ");
        }

        columns.setLength(columns.length() - 2);
        placeholders.setLength(placeholders.length() - 2);
        return "INSERT INTO "
                + tableName
                + " ("
                + columns
                + ") VALUES ("
                + placeholders
                + ")";
    }

    static void update(Connection conn, Map<String, Object> values, String tableName, String pkName, long id) throws SQLException {
        Map<String, Object> orderedValues = new LinkedHashMap<>(values);
        try {
            String sql = buildUpdateString(tableName, pkName, orderedValues);
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
            String sql = buildInsertString(tableName, orderedValues);
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
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
