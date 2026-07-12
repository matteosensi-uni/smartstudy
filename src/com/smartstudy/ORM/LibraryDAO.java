package com.smartstudy.ORM;

import com.smartstudy.DomainModel.Library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibraryDAO extends BaseDAO{
    public static final String tableName = "library";
    public static final String pkName = "id_library";
    public LibraryDAO(Connection conn) { super(conn); }
    public Library getLibraryById(long libraryId) throws RuntimeException{
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM library WHERE id_library = ?
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createLibraryFromResultSet(rs);
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Library getLibraryByAdmin(long adminId) throws RuntimeException{
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT library.*
                FROM library LEFT JOIN admin ON admin.id_library = library.id_library
                WHERE admin.user_id = ?
            """
            )){
            ps.setLong(1, adminId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createLibraryFromResultSet(rs);
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Library createLibraryFromResultSet(ResultSet rs) throws SQLException {
            return Library.valueOf(
                    rs.getLong("id_library"),
                    rs.getString("name"),
                    rs.getTime("opening_time").toLocalTime(),
                    rs.getTime("closing_time").toLocalTime(),
                    rs.getString("street"),
                    rs.getString("number"),
                    rs.getString("city")
            );
    }
}
