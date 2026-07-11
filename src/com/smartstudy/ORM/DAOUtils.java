package com.smartstudy.ORM;

import com.smartstudy.utils.SQLUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.Map;

final class DAOUtils {
    private DAOUtils() {}
    static void update(Connection conn, Map<String, Object> values, String tableName, String pkName, long id){
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    static void insert(Connection conn, Map<String, Object> values, String tableName){
        Map<String, Object> orderedValues = new LinkedHashMap<>(values);
        try {
            String sql = SQLUtils.buildInsertString(tableName, orderedValues);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int index = 1;
                for (Map.Entry<String, Object> entry : orderedValues.entrySet()) {
                    ps.setObject(index++, entry.getValue());
                }
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
