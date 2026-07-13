package com.smartstudy.db;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private TransactionManager() {}
    public static void executeInTransaction(Runnable work)  {
        Connection conn = ConnectionManager.getInstance().getConnection();
        try {
            try {
                conn.setAutoCommit(false);
                work.run();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("le modifiche non sono andate a buon fine", e);
            } finally {
                conn.setAutoCommit(true);
            }
        }catch (SQLException e){
            throw new DataAccessException("le modifiche non sono andate a buon fine", e);
        }
    }
}
