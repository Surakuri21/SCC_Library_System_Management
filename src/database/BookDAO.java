package database;

import model.Books;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;

    public BookDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void addBook(Books book) {
        String query = "INSERT INTO books (title, author, isbn, quantity, category) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setString(5, book.getCategory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Books book) {
        String query = "UPDATE books SET title = ?, author = ?, isbn = ?, quantity = ?, category = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setString(5, book.getCategory());
            pstmt.setInt(6, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBook(int id) {
        String query = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Books getBookById(int id) {
        String query = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Books> getAllBooks() {
        List<Books> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public int getTotalBooksCount() {
        String query = "SELECT COUNT(*) FROM books";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Books> searchBooks(String keyword) {
        List<Books> books = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }

        String[] terms = keyword.trim().split("\\s+");
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM books WHERE ");
        
        for (int i = 0; i < terms.length; i++) {
            if (i > 0) queryBuilder.append(" AND ");
            queryBuilder.append("(title LIKE ? OR author LIKE ? OR isbn LIKE ? OR category LIKE ?)");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;
            for (String term : terms) {
                String pattern = "%" + term + "%";
                pstmt.setString(paramIndex++, pattern);
                pstmt.setString(paramIndex++, pattern);
                pstmt.setString(paramIndex++, pattern);
                pstmt.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private Books mapResultSetToBook(ResultSet rs) throws SQLException {
        String category = "General";
        try {
            category = rs.getString("category");
        } catch (SQLException e) {
        }
        
        return new Books(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("quantity"),
                category
        );
    }
}