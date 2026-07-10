package com.smartstudy.ORM;
import com.smartstudy.DomainModel.Student;
import com.smartstudy.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    private StudentDAO() {}
    public static Student getStudentById(long adminId){
        try{
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                SELECT app_user.*, student.card_active
                FROM student LEFT JOIN app_user ON student.user_id = app_user.user_id
                WHERE student.user_id = ?
            """
            );
            ps.setLong(1, adminId);
            ResultSet rs = ps.executeQuery();
            return createStudentFromResultSet(rs);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existsById(long studentId){
        try{
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                SELECT 1
                FROM student
                WHERE student.user_id = ?
            """
            );
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        if(rs.next()){
            return new Student(
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getString("surname"),
                    rs.getString("email"),
                    rs.getBoolean("card_active")
            );
        }
        return null;
    }
}
