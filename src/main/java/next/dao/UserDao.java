package next.dao;

import next.model.User;
import next.support.jdbc.JdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public void insert(User user) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
        jdbcTemplate.executeUpdate(sql, user.getUserId(), user.getPassword(), user.getName(), user.getEmail());
//        jdbcTemplate.executeUpdate(sql, (pstmt -> {
//            pstmt.setString(1, user.getUserId())
//            pstmt.setString(2, user.getPassword());
//            pstmt.setString(3, user.getName());
//            pstmt.setString(4, user.getEmail());
//        }));
    }

    public void update(User user) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "UPDATE USERS SET password = ?, name= ?, email= ? WHERE userId = ?";
        jdbcTemplate.executeUpdate(sql, user.getPassword(), user.getName(), user.getEmail(), user.getUserId());
//        jdbcTemplate.executeUpdate(sql, (pstmt -> {
//            pstmt.setString(1, user.getPassword());
//            pstmt.setString(2, user.getName());
//            pstmt.setString(3, user.getEmail());
//            pstmt.setString(4, user.getUserId());
//        }));
    }

    public List<User> findAll() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "SELECT userId, password, name, email FROM USERS";
        return jdbcTemplate.executeQuery(
                sql,
                rs -> {
                    List<User> users = new ArrayList<>();

                    while (rs.next()) {
                        users.add(new User(
                                rs.getString("userId"),
                                rs.getString("password"),
                                rs.getString("name"),
                                rs.getString("email")
                        ));
                    }

                    return users;
                }
        );
    }

    public User findByUserId(String userId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        String sql = "SELECT userId, password, name, email FROM USERS WHERE userid=?";
        return jdbcTemplate.executeQuery(
                sql,
                rs -> {
                    User user = null;

                    if (rs.next()) {
                        user = new User(
                                rs.getString("userId"),
                                rs.getString("password"),
                                rs.getString("name"),
                                rs.getString("email")
                        );
                    }

                    return user;
                },
                userId
        );
    }
}
