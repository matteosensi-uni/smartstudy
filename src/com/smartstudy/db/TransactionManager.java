package com.smartstudy.db;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionManager {
    private TransactionManager() {}

    public static void executeInTransaction(Runnable work)  {
        executeInTransaction(() -> {
            work.run();
            return null;
        });
    }

    public static <T> T executeInTransaction(Supplier<T> work) {
        Connection conn = ConnectionManager.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            T result = work.get();
            conn.commit();
            return result;
        } catch (RuntimeException e) {
            rollback(conn);
            throw e;
        } catch (SQLException e) {
            rollback(conn);
            throw new DataAccessException("le modifiche non sono andate a buon fine", e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) { }
        }
    }

    private static void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) { }
    }
}
