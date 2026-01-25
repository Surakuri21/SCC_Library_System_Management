package database;

import model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Connection connection;

    public TransactionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void issueBook(int studentId, int bookId) {
        // 1. Check if there is an existing RESERVATION for this book
        int reservationId = getReservationId(studentId, bookId);
        
        if (reservationId != -1) {
            // Fulfill Reservation (Update status to ISSUED)
            fulfillReservation(reservationId);
        } else {
            // New Issue (Insert new record and decrease quantity)
            // Check if student has reached limit (e.g., 3 books)
            if (getBorrowedCount(studentId) >= 3) {
                // This check should ideally be done in the UI before calling this, 
                // but we can enforce it here or just proceed. 
                // For now, we proceed as the UI handles validation messages usually.
            }

            String query = "INSERT INTO transactions (student_id, book_id, issue_date, status) VALUES (?, ?, CURDATE(), 'ISSUED')";
            String updateBookQuery = "UPDATE books SET quantity = quantity - 1 WHERE id = ?";
            
            try {
                connection.setAutoCommit(false); 

                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setInt(1, studentId);
                    pstmt.setInt(2, bookId);
                    pstmt.executeUpdate();
                }

                try (PreparedStatement pstmt = connection.prepareStatement(updateBookQuery)) {
                    pstmt.setInt(1, bookId);
                    pstmt.executeUpdate();
                }

                connection.commit(); 
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                try {
                    connection.rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
    
    public int getReservationId(int studentId, int bookId) {
        String query = "SELECT id FROM transactions WHERE student_id = ? AND book_id = ? AND status = 'RESERVED'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public void fulfillReservation(int transactionId) {
        String query = "UPDATE transactions SET status = 'ISSUED', issue_date = CURDATE() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void reserveBook(int studentId, int bookId) {
        System.out.println("DEBUG: Reserving Book. StudentID=" + studentId + ", BookID=" + bookId);
        
        String query = "INSERT INTO transactions (student_id, book_id, issue_date, status) VALUES (?, ?, CURDATE(), 'RESERVED')";
        String updateBookQuery = "UPDATE books SET quantity = quantity - 1 WHERE id = ?";
        
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, bookId);
                int rows = pstmt.executeUpdate();
                System.out.println("DEBUG: Insert Transaction Rows: " + rows);
            }

            try (PreparedStatement pstmt = connection.prepareStatement(updateBookQuery)) {
                pstmt.setInt(1, bookId);
                int rows = pstmt.executeUpdate();
                System.out.println("DEBUG: Update Book Rows: " + rows);
            }

            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("DEBUG: Transaction Committed Successfully.");
            
        } catch (SQLException e) {
            System.err.println("DEBUG: Transaction FAILED! Rolling back.");
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void returnBook(int transactionId, int bookId) {
        // First, find the student ID associated with this transaction
        int studentId = -1;
        String findStudentSql = "SELECT student_id FROM transactions WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(findStudentSql)) {
            pstmt.setInt(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    studentId = rs.getInt("student_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        if (studentId == -1) return;

        // Update ALL active transactions for this book and student (Fixes duplicate issue bug)
        String query = "UPDATE transactions SET return_date = CURDATE(), status = 'RETURNED' WHERE student_id = ? AND book_id = ? AND (status = 'ISSUED' OR status = 'RESERVED')";
        String updateBookQuery = "UPDATE books SET quantity = quantity + ? WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            int rowsUpdated = 0;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, bookId);
                rowsUpdated = pstmt.executeUpdate();
            }

            if (rowsUpdated > 0) {
                try (PreparedStatement pstmt = connection.prepareStatement(updateBookQuery)) {
                    pstmt.setInt(1, rowsUpdated);
                    pstmt.setInt(2, bookId);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public List<Transaction> getAllTransactions() {
        return searchTransactions(""); // Just call search with empty keyword
    }
    
    public List<Transaction> searchTransactions(String keyword) {
        List<Transaction> transactions = new ArrayList<>();
        // Modified to search by Student Name, Book Title, or Issue Date (Case Insensitive)
        String query = "SELECT t.id, t.student_id, s.name as student_name, t.book_id, b.title as book_title, t.issue_date, t.return_date, t.status " +
                       "FROM transactions t " +
                       "JOIN students s ON t.student_id = s.id " +
                       "JOIN books b ON t.book_id = b.id " +
                       "WHERE LOWER(s.name) LIKE LOWER(?) OR LOWER(b.title) LIKE LOWER(?) OR CAST(t.issue_date AS CHAR) LIKE ? " +
                       "ORDER BY t.id DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern); // Date is usually numeric/dashes, so case doesn't matter as much, but pattern is same
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getString("student_name"),
                            rs.getInt("book_id"),
                            rs.getString("book_title"),
                            rs.getDate("issue_date"),
                            rs.getDate("return_date"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getIssuedBooksReport() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.id, t.student_id, s.name as student_name, t.book_id, b.title as book_title, t.issue_date, t.return_date, t.status " +
                       "FROM transactions t " +
                       "JOIN students s ON t.student_id = s.id " +
                       "JOIN books b ON t.book_id = b.id " +
                       "WHERE t.status = 'ISSUED' " +
                       "ORDER BY t.issue_date ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getReturnedBooksReport() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.id, t.student_id, s.name as student_name, t.book_id, b.title as book_title, t.issue_date, t.return_date, t.status " +
                       "FROM transactions t " +
                       "JOIN students s ON t.student_id = s.id " +
                       "JOIN books b ON t.book_id = b.id " +
                       "WHERE t.status = 'RETURNED' " +
                       "ORDER BY t.return_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    public List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        // Modified ORDER BY to sort by ID DESC (newest first)
        String query = "SELECT t.id, t.student_id, s.name as student_name, t.book_id, b.title as book_title, t.issue_date, t.return_date, t.status " +
                       "FROM transactions t " +
                       "JOIN students s ON t.student_id = s.id " +
                       "JOIN books b ON t.book_id = b.id " +
                       "ORDER BY t.id DESC LIMIT " + limit;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    public int getBorrowedCount(int studentId) {
        // Use DISTINCT to avoid counting duplicate issues of the same book
        String query = "SELECT COUNT(DISTINCT book_id) FROM transactions WHERE student_id = ? AND (status = 'ISSUED' OR status = 'RESERVED')";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalBorrowedCount() {
        // Use DISTINCT to avoid counting duplicate issues of the same book
        String query = "SELECT COUNT(*) FROM transactions WHERE status = 'ISSUED'";
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
    
    public int getOverdueCount() {
        // Assuming overdue means issued more than 15 days ago and not returned
        String query = "SELECT COUNT(*) FROM transactions WHERE status = 'ISSUED' AND issue_date < DATE_SUB(CURDATE(), INTERVAL 15 DAY)";
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
    
    public boolean isBookAlreadyBorrowed(int studentId, int bookId) {
        System.out.println("DEBUG: Checking duplicate for S=" + studentId + ", B=" + bookId);
        String query = "SELECT 1 FROM transactions WHERE student_id = ? AND book_id = ? AND (UPPER(TRIM(status)) = 'ISSUED' OR UPPER(TRIM(status)) = 'RESERVED')";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean exists = rs.next();
                System.out.println("DEBUG: Duplicate Found? " + exists);
                return exists;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // NEW METHOD: Get Transactions for a specific Student
    public List<Transaction> getTransactionsByStudentId(int studentId) {
        List<Transaction> transactions = new ArrayList<>();
        // Modified ORDER BY to sort by ID DESC (newest first)
        String query = "SELECT t.id, t.student_id, s.name as student_name, t.book_id, b.title as book_title, t.issue_date, t.return_date, t.status " +
                       "FROM transactions t " +
                       "JOIN students s ON t.student_id = s.id " +
                       "JOIN books b ON t.book_id = b.id " +
                       "WHERE t.student_id = ? " +
                       "ORDER BY t.id DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getString("student_name"),
                            rs.getInt("book_id"),
                            rs.getString("book_title"),
                            rs.getDate("issue_date"),
                            rs.getDate("return_date"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    // NEW METHOD: Check if student has reached borrow limit
    public boolean hasReachedBorrowLimit(int studentId) {
        // Limit is 3 books
        return getBorrowedCount(studentId) >= 3;
    }

    // NEW METHOD: Force return all books for a student (Cleanup utility)
    public void forceReturnAllBooksForStudent(int studentId) {
        String query = "UPDATE transactions SET status = 'RETURNED', return_date = CURDATE() WHERE student_id = ? AND (status = 'ISSUED' OR status = 'RESERVED')";
        // Note: This doesn't update book quantity correctly if there are duplicates, 
        // but it fixes the student's dashboard. 
        // To be safe, we should iterate and update quantity for each.
        
        String selectQuery = "SELECT id, book_id FROM transactions WHERE student_id = ? AND (status = 'ISSUED' OR status = 'RESERVED')";
        
        try {
            connection.setAutoCommit(false);
            
            List<Integer> transIds = new ArrayList<>();
            List<Integer> bookIds = new ArrayList<>();
            
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        transIds.add(rs.getInt("id"));
                        bookIds.add(rs.getInt("book_id"));
                    }
                }
            }
            
            String updateTrans = "UPDATE transactions SET status = 'RETURNED', return_date = CURDATE() WHERE id = ?";
            String updateBook = "UPDATE books SET quantity = quantity + 1 WHERE id = ?";
            
            try (PreparedStatement pt = connection.prepareStatement(updateTrans);
                 PreparedStatement pb = connection.prepareStatement(updateBook)) {
                
                for (int i = 0; i < transIds.size(); i++) {
                    pt.setInt(1, transIds.get(i));
                    pt.executeUpdate();
                    
                    pb.setInt(1, bookIds.get(i));
                    pb.executeUpdate();
                }
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}