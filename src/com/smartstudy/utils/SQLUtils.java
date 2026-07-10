package com.smartstudy.utils;
import java.util.Map;

public class SQLUtils {
    private SQLUtils() {}
    public static String buildUpdateString(String tableName, String pk_name, Map<String, Object> values){
        StringBuilder placeholders = new StringBuilder();
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

    public static String buildUInsertString(String tableName, Map<String, Object> values){
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

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
}
