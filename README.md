# SCC Library Management System

A comprehensive Library Management System built with Java Swing, designed to streamline library operations for both administrators and students. This system facilitates book management, member tracking, and efficient issue/return transactions.

## 🚀 Features

### 🔐 Authentication
*   **Secure Login:** Role-based access control (Admin/Student).
*   **Student Registration:** Easy sign-up process for new members.

### 👨‍💼 Admin Dashboard
*   **Dashboard Overview:** Real-time statistics on total books, active members, borrowed books, and overdue items.
*   **Book Management:**
    *   Add, Update, and Delete books.
    *   Real-time search functionality.
    *   Track book quantities and availability.
*   **Member Management:**
    *   View all registered students.
    *   Search and filter student records.
    *   Remove student accounts (cascading delete for associated user data).
*   **Transaction Management:**
    *   **Issue Books:** Streamlined process to issue books to students.
    *   **Return Books:** Handle book returns with automatic status updates.
    *   **Smart Sync:** Automatically clears duplicate "ghost" records upon return.
*   **Announcements:** Post updates visible to all students.

### 🎓 Student Dashboard
*   **Personalized View:** See your own borrowed books and overdue status.
*   **Book Catalog:** Browse and search the library's collection.
*   **Reservation System:** Reserve books directly from the catalog (subject to availability and borrow limits).
*   **Borrow History:** View a complete history of borrowed and returned books.
*   **Announcements:** Stay updated with the latest library news.

## 🛠️ Technology Stack

*   **Language:** Java (JDK 21+)
*   **GUI Framework:** Java Swing (Custom styled components)
*   **Database:** MySQL (via JDBC)
*   **Architecture:** DAO (Data Access Object) Pattern
*   **IDE:** IntelliJ IDEA

## ⚙️ Setup & Installation

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/Surakuri21/SCC_Library_System_Management.git
    ```

2.  **Database Configuration**
    *   Import the provided SQL schema (if available) into your MySQL server.
    *   Update `src/database/DatabaseConnection.java` with your DB credentials:
        ```java
        private static final String URL = "jdbc:mysql://localhost:3306/scc_library_db";
        private static final String USER = "root";
        private static final String PASSWORD = "your_password";
        ```

3.  **Run the Application**
    *   Open the project in IntelliJ IDEA.
    *   Run `src/Main.java`.

## 📝 Usage Guide

*   **Default Admin Credentials:** (Setup in database)
    *   Username: `admin`
    *   Password: `admin123` (Change immediately for production)

*   **Borrowing Rules:**
    *   Students can borrow/reserve a maximum of **3 books** at a time.
    *   Books are considered **Overdue** after 15 days.

## 🤝 Contributing

1.  Fork the project.
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
