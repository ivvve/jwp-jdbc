package next.support.jdbc;

import core.jdbc.ConnectionManager;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    public void executeUpdate(String sql, PreparedStatementSetter pss) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pss.setParameters(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        } finally {
            this.closeIfNotNull(pstmt);

            if (pstmt != null) {
                pstmt.close();
            }

            if (con != null) {
                con.close();
            }
        }
    }

    private void closeIfNotNull(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
    }

    public void executeUpdate(String sql, Object... parameters) {
        final PreparedStatementSetter preparedStatementSetter = createPreparedStatementSetter(parameters);
        this.executeUpdate(sql, preparedStatementSetter);

//        Connection con = null;
//        PreparedStatement pstmt = null;
//        try {
//            con = ConnectionManager.getConnection();
//            pstmt = con.prepareStatement(sql);
//
//            for (int i = 0; i < parameters.length; i++) {
//                final Object parameter = parameters[i];
//                pstmt.setObject(i + 1, parameter);
//            }
//
//            pstmt.executeUpdate();
//        } finally {
//            if (pstmt != null) {
//                pstmt.close();
//            }
//
//            if (con != null) {
//                con.close();
//            }
//        }
    }

    public <T> T executeQuery(String sql, RowMapper<T> rm) throws SQLException {
        return executeQuery(sql, null, rm);
    }

    public <T> T executeQuery(String sql, PreparedStatementSetter pss, RowMapper<T> rm) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(sql);
            if (pss != null) {
                pss.setParameters(pstmt);
            }
            rs = pstmt.executeQuery();

            return rm.map(rs);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

    public <T> T executeQuery(String sql, RowMapper<T> rm, Object... parameters) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(sql);

            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                pstmt.setObject(i + 1, parameter);
            }

            rs = pstmt.executeQuery();

            return rm.map(rs);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

    public <T> List<T> executeQueryBatch(String sql, RowMapper<T> rm) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<T> list = new ArrayList<>();

            while (rs.next()) {
                T row = rm.map(rs);
                list.add(row);
            }

            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

    private PreparedStatementSetter createPreparedStatementSetter(final Object[] parameters) {
        return pstmt -> {
            for (int i = 0; i < parameters.length; i++) {
                final Object parameter = parameters[i];
                pstmt.setObject(i + 1, parameter);
            }
        };
    }
}
