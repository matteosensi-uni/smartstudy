package com.smartstudy.ORM;
import com.smartstudy.domainModel.Student;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO extends BaseDAO{
    public static final String tableName = "student";
    public static final String pkName = "user_id";

    public StudentDAO(Connection conn) { super(conn); }
    public Student getStudentById(long studentId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT app_user.*, student.card_active
                FROM student LEFT JOIN app_user ON student.user_id = app_user.user_id
                WHERE student.user_id = ?
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createStudentFromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare lo studente", e);
        }
    }
    public boolean existsById(long studentId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM student
                WHERE student.user_id = ?
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare l'utente", e);
        }
    }
    private Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        return Student.valueOf(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getBoolean("card_active")
        );
    }
}
