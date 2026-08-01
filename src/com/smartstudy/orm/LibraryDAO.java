package com.smartstudy.orm;

import com.smartstudy.domainModel.Library;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LibraryDAO extends BaseDAO{
    public LibraryDAO(Connection conn) { super(conn); }
    public Optional<Library> getLibraryById(long libraryId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM library WHERE id_library = ?
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(createLibraryFromResultSet(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la libreria", e);
        }
    }

    public Optional<Library> getLibraryByAdmin(long adminId) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT library.*
                FROM library LEFT JOIN admin ON admin.id_library = library.id_library
                WHERE user_id = ?
            """
        )){
            ps.setLong(1, adminId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(createLibraryFromResultSet(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la libreria", e);
        }
    }

    private Library createLibraryFromResultSet(ResultSet rs) throws SQLException {
        AdminDAO adminDAO = new AdminDAO(conn);
        return Library.valueOf(
                    rs.getLong("id_library"),
                    rs.getString("name"),
                    rs.getTime("opening_time").toLocalTime(),
                    rs.getTime("closing_time").toLocalTime(),
                    rs.getString("street"),
                    rs.getString("number"),
                    rs.getString("city"),
                    adminDAO.getAdminsByLibraryId(rs.getLong("id_library"))
            );
    }

}
