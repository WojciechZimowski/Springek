package org.example.repositories.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
@Profile("jdbc")
public class UserJDBCRepository implements IUserRepository {
    private final DataSource dataSource;

    public UserJDBCRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {

        String sql = "INSERT INTO users (id, login, password_hash, role) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET login = EXCLUDED.login, " +
                "password_hash = EXCLUDED.password_hash, role = EXCLUDED.role";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());

            stmt.executeUpdate();

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getString("id"))
                .login(rs.getString("login"))
                .passwordHash(rs.getString("password_hash"))
                .role(Role.valueOf(rs.getString("role")))
                .build();
    }
}