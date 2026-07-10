package com.smartstudy.ORM;

import com.smartstudy.DomainModel.StudyArea;
import com.smartstudy.DomainModel.enums.StudyAreaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class StudyAreaDAO extends BaseDAO implements Updatable{
    public static final String tableName = "study_area";
    public static final String pkName = "id_area";

    public StudyAreaDAO(Connection conn) {
        super(conn);
    }
    public StudyArea getStudyAreaById(long studyAreaId){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM study_area
                WHERE study_area.id_area = ?
            """
            );
            ps.setLong(1, studyAreaId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return createStudyAreaFromResultSet(rs);
            else return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<StudyArea> getLibraryStudyAreas(long library_id){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM study_area
                WHERE study_area.id_library = ?
            """
            );
            ps.setLong(1, library_id);
            ResultSet rs = ps.executeQuery();
            ArrayList<StudyArea> res = new ArrayList<>();
            while(rs.next())
                res.add(createStudyAreaFromResultSet(rs));
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public StudyArea getStudyAreaBySeat(long seat_id){
        try{
            PreparedStatement ps = conn.prepareStatement("""
                SELECT study_area.*
                FROM study_area LEFT JOIN seat ON study_area.id_area = seat.id_area
                WHERE id_seat = ?
            """
            );
            ps.setLong(1, seat_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return createStudyAreaFromResultSet(rs);
            }else
                return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private StudyArea createStudyAreaFromResultSet(ResultSet rs) throws SQLException {
        return new StudyArea(
                rs.getLong("id_area"),
                rs.getString("name"),
                rs.getInt("floor"),
                StudyAreaType.valueOf(rs.getString("type")),
                rs.getLong("id_library"),
                rs.getLong("id_policy")
        );

    }

    @Override
    public void update(Map<String, Object> values, long id) {
        DAOUtils.update(conn, values, tableName, pkName, id);
    }
}
