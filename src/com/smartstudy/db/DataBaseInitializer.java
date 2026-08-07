package com.smartstudy.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class DataBaseInitializer {

    private DataBaseInitializer() {}

    private static void executeScript(String path, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));

            StringBuilder query = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                if(line.trim().startsWith("-- ")) {
                    continue;
                }
                query.append(line).append(" ");
                if(line.trim().endsWith(";")) {
                    statement.execute(query.toString().trim());
                    query = new StringBuilder();
                }
            }
            br.close();
            statement.close();
        }catch (IOException e){
            throw new RuntimeException("Impossibile aprire il file: "+ path, e);
        }

    }


    public static void prepareTestSchema() {
        Connection connection = ConnectionManager.getInstance().getConnection();
        try(Statement statement = connection.createStatement()){
            statement.execute("""
                    DROP SCHEMA IF EXISTS test CASCADE;
                    CREATE SCHEMA test;
                    SET search_path TO test;
                    """);
            executeScript("sql/schema.sql", connection);
            executeScript("sql/default.sql", connection);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'inizializzazione dello schema per il testing", e);
        }
    }

    public static void resetData(){
        Connection connection = ConnectionManager.getInstance().getConnection();
        try(Statement statement = connection.createStatement()){
            statement.execute("""
                    SET search_path TO test;
                    TRUNCATE TABLE
                        abandonment_report,
                        temporary_leave,
                        reservation,
                        access_session,
                        seat,
                        study_area,
                        time_policy,
                        admin,
                        library,
                        student,
                        app_user
                        RESTART IDENTITY CASCADE;
                    """);
            executeScript("sql/default.sql", connection);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'inizializzazione dello schema per il testing", e);
        }
    }

    public static void main(String[] args) {
        DataBaseInitializer.prepareTestSchema();
        DataBaseInitializer.resetData();
    }
}