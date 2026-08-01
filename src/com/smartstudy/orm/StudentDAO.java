package com.smartstudy.orm;
import com.smartstudy.domainModel.Student;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class StudentDAO extends BaseDAO{
    public StudentDAO(Connection conn) { super(conn); }
    public Optional<Student> getStudentById(long studentId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT app_user.*, student.card_active
                FROM student LEFT JOIN app_user ON student.user_id = app_user.user_id
                WHERE student.user_id = ?
            """
            )){
            ps.setLong(1, studentId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(createStudentFromResultSet(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare lo studente", e);
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
