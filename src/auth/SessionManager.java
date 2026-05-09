package auth;

import java.sql.*;
import java.util.UUID;
import auth.database.DatabaseConnection;

public class SessionManager {

    // Создание новой сессии
    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        String sql = "INSERT INTO sessions (session_id, user_id, expires_at) VALUES (?, ?, ?)";
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setInt(2, user.getId());
            stmt.setTimestamp(3, expiresAt);
            stmt.executeUpdate();
            return sessionId;
        } catch (SQLException e) {
            System.err.println("Create session error: " + e.getMessage());
            return null;
        }
    }

    // Проверка валидности сессии
    public boolean isValidSession(String sessionId) {
        if (sessionId == null) return false;

        String sql = "SELECT expires_at FROM sessions WHERE session_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expiresAt = rs.getTimestamp("expires_at");
                if (expiresAt.after(new Timestamp(System.currentTimeMillis()))) {
                    return true;
                } else {
                    invalidateSession(sessionId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Check session error: " + e.getMessage());
        }
        return false;
    }

    // Получение пользователя по ID сессии
    public User getUserFromSession(String sessionId) {
        if (sessionId == null) return null;

        String sql = "SELECT u.id, u.username, u.email, u.created_at, u.last_login " +
                "FROM sessions s JOIN users u ON s.user_id = u.id " +
                "WHERE s.session_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("last_login")
                );
            }
        } catch (SQLException e) {
            System.err.println("Get user from session error: " + e.getMessage());
        }
        return null;
    }

    // Удаление сессии (выход)
    public void invalidateSession(String sessionId) {
        if (sessionId == null) return;

        String sql = "DELETE FROM sessions WHERE session_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Invalidate session error: " + e.getMessage());
        }
    }
}