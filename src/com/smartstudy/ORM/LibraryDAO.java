package com.smartstudy.ORM;

import com.smartstudy.DomainModel.Library;
import com.smartstudy.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;

public class LibraryDAO {
    public static Library selectById(int libraryId){
        try{
            Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM library WHERE id_library = ?
            """
            );
            ps.setInt(1, libraryId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new Library(
                        libraryId,
                        rs.getString("name"),
                        rs.getTime("opening_time").toLocalTime(),
                        rs.getTime("closing_time").toLocalTime(),
                        rs.getString("street"),
                        rs.getString("number"),
                        rs.getString("city")
                        );
            }else{
                throw new IllegalArgumentException("Nome utente o password errati");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
