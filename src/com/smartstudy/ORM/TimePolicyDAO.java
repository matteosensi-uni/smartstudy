package com.smartstudy.ORM;

import com.smartstudy.DomainModel.TimePolicy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TimePolicyDAO extends BaseDAO{
    public TimePolicyDAO(Connection conn) {
        super(conn);
    }

    public TimePolicy getTimePolicyByStudyArea(long studyAreaId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM
                time_policy LEFT JOIN study_area ON time_policy.id_policy = study_area.id_policy
                WHERE study_area.id_area = ?
            """
            )){
            ps.setLong(1, studyAreaId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createTimePolicyFromResultSet(rs);
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TimePolicy getTimePolicyById(long policyId){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM time_policy WHERE id_policy = ?
            """
            )){
            ps.setLong(1, policyId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createTimePolicyFromResultSet(rs);
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<TimePolicy> getAllPolicies(){
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM time_policy
            """
            )){
            try(ResultSet rs = ps.executeQuery()){
                ArrayList<TimePolicy> res = new ArrayList<>();
                while(rs.next()){
                    res.add(createTimePolicyFromResultSet(rs));
                }
                return res;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TimePolicy createTimePolicyFromResultSet(ResultSet rs) throws SQLException {
        return TimePolicy.valueOf(
                rs.getLong("id_policy"),
                rs.getInt("max_temporary_leave_min"),
                rs.getInt("max_temporary_leave_times"),
                rs.getString("name")
        );
    }

}
