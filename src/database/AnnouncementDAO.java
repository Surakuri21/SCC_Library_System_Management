package database;

import model.Announcement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    private Connection connection;

    public AnnouncementDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void addAnnouncement(String title, String content, String type) {
        String query = "INSERT INTO announcements (title, content, type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Announcement> getLatestAnnouncements() {
        List<Announcement> list = new ArrayList<>();
        String query = "SELECT * FROM announcements ORDER BY created_at DESC LIMIT 5";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Announcement(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}