package core.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    public <T> T queryForObject(String sql, RowMapper<T> mapper, Object... parameters) {
        List<T> list = this.queryForList(sql, mapper, this.toPreparedStatementParameterSetter(parameters));
        return list.isEmpty() ? null : list.get(0);
    }

    public <T> T queryForObject(String sql, RowMapper<T> mapper, PreparedStatementParameterSetter setter) {
        List<T> list = this.queryForList(sql, mapper, setter);
        return list.isEmpty() ? null : list.get(0);
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> mapper, Object... parameters) {
        return this.queryForList(sql, mapper, this.toPreparedStatementParameterSetter(parameters));
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> mapper, PreparedStatementParameterSetter setter) {
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            // execute sql query
            setter.set(pstmt);
            ResultSet rs = pstmt.executeQuery();

            // get and map data from sql result
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                T mappedData = mapper.map(rs);
                list.add(mappedData);
            }
            rs.close();
            return list;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void update(String sql, Object... parameters) {
        this.update(sql, this.toPreparedStatementParameterSetter(parameters));
    }

    public void update(String sql, PreparedStatementParameterSetter setter) {
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            setter.set(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private PreparedStatementParameterSetter toPreparedStatementParameterSetter(Object... parameters) {
        return pstmt -> {
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                pstmt.setObject(i + 1, parameter);
            }
        };
    }
}
