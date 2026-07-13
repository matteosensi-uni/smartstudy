package com.smartstudy.ORM;

import com.smartstudy.domainModel.StudyArea;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudyAreaDAO extends BaseDAO implements Updatable<StudyArea>{
    public static final String tableName = "study_area";
    public static final String pkName = "id_area";

    public StudyAreaDAO(Connection conn) {
        super(conn);
    }
    public StudyArea getStudyAreaById(long studyAreaId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM study_area
                WHERE study_area.id_area = ?
            """
            )){
            ps.setLong(1, studyAreaId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return createStudyAreaFromResultSet(rs);
                else return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare l'area studio", e);
        }
    }

    public ArrayList<StudyArea> getLibraryStudyAreas(long libraryId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT *
                FROM study_area
                WHERE study_area.id_library = ?
            """
            )){
            ps.setLong(1, libraryId);
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<StudyArea> res = new ArrayList<>();
                while(rs.next())
                    res.add(createStudyAreaFromResultSet(rs));
                return res;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le aree studio", e);
        }
    }

    public StudyArea getStudyAreaBySeat(long seatId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT study_area.*
                FROM study_area LEFT JOIN seat ON study_area.id_area = seat.id_area
                WHERE id_seat = ?
            """
            )){
            ps.setLong(1, seatId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createStudyAreaFromResultSet(rs);
                }else
                    return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare l'area studio dal posto", e);
        }
    }

    private StudyArea createStudyAreaFromResultSet(ResultSet rs) throws SQLException {
        return StudyArea.valueOf(
                rs.getLong("id_area"),
                rs.getString("name"),
                rs.getInt("floor"),
                StudyAreaType.valueOf(rs.getString("type")),
                rs.getLong("id_library"),
                rs.getLong("id_policy")
        );

    }

    @Override
    public void update(StudyArea studyArea) {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("name", studyArea.getName());
            values.put("type", studyArea.getType().name());
            values.put("id_policy", studyArea.getTimePolicyId());
            DAOUtils.update(conn, values, tableName, pkName, studyArea.getId());
        } catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile aggriornare l'area studio", e);
        }
    }
}
