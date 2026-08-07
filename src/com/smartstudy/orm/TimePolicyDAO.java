package com.smartstudy.orm;

import com.smartstudy.domainModel.TimePolicy;
import com.smartstudy.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class TimePolicyDAO extends BaseDAO{
    public TimePolicyDAO(Connection conn) {
        super(conn);
    }

    public Optional<TimePolicy> getTimePolicyByName(String name) {
        try(PreparedStatement ps = conn.prepareStatement("""
                SELECT * FROM time_policy WHERE name = ?
            """
        )){
            ps.setString(1, name);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return Optional.of(createTimePolicyFromResultSet(rs));
                return Optional.empty();
            }
        }  catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare la regola", e);
        }
    }

    public ArrayList<TimePolicy> getAllPolicies() {
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
        }  catch (SQLException e) {
            throw new DataAccessException("Non è stato possibile recuperare le time policies", e);
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
