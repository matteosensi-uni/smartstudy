package com.smartstudy.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class DataBaseInitializer {

    private final Connection connection;

    public DataBaseInitializer(Connection connection) {
        this.connection = connection;
    }

    private void executeScript(String path) throws SQLException {
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


    public void prepareTestSchema() {
        try(Statement statement = connection.createStatement()){
            statement.execute("""
                    DROP SCHEMA IF EXISTS test CASCADE;
                    CREATE SCHEMA test;
                    SET search_path TO test;
                    """);
            executeScript("sql/schema.sql");
            executeScript("sql/default.sql");
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'inizializzazione dello schema per il testing", e);
        }
    }

    public static void main(String[] args) {
        DataBaseInitializer dataBaseInitializer = new DataBaseInitializer(ConnectionManager.getInstance().getConnection());
        dataBaseInitializer.prepareTestSchema();
    }
}