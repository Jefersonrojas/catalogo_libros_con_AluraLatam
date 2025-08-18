import java.sql.*;

public class Database {
    private static Connection conn = null;
    private static final String DB_URL = "jdbc:sqlite:literals.db";

    public static void init() throws SQLException, ClassNotFoundException {
      
        conn = DriverManager.getConnection(DB_URL);
        createTables();
    }

    private static void createTables() throws SQLException {
        String createBooks = "CREATE TABLE IF NOT EXISTS books (" +
                "gutenberg_id INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "language TEXT" +
                ");";
        String createAuthors = "CREATE TABLE IF NOT EXISTS authors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_id INTEGER, " +
                "name TEXT, " +
                "FOREIGN KEY(book_id) REFERENCES books(gutenberg_id)" +
                ");";
        try (Statement st = conn.createStatement()) {
            st.execute(createBooks);
            st.execute(createAuthors);
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
