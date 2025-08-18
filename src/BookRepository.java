import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public void saveBook(Book book) throws SQLException {
        Connection c = Database.getConnection();
        String insertBook = "INSERT OR REPLACE INTO books (gutenberg_id, title, language) VALUES (?, ?, ?);";
        try (PreparedStatement pb = c.prepareStatement(insertBook)) {
            pb.setInt(1, book.getGutenbergId());
            pb.setString(2, book.getTitle());
            pb.setString(3, book.getLanguage());
            pb.executeUpdate();
        }

        String delAuth = "DELETE FROM authors WHERE book_id = ?;";
        try (PreparedStatement pd = c.prepareStatement(delAuth)) {
            pd.setInt(1, book.getGutenbergId());
            pd.executeUpdate();
        }
        String insAuth = "INSERT INTO authors (book_id, name) VALUES (?, ?);";
        if (book.getAuthors() != null) {
            try (PreparedStatement pa = c.prepareStatement(insAuth)) {
                for (Author a : book.getAuthors()) {
                    pa.setInt(1, book.getGutenbergId());
                    pa.setString(2, a.getName());
                    pa.executeUpdate();
                }
            }
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        Connection c = Database.getConnection();
        String q = "SELECT b.gutenberg_id, b.title, b.language, GROUP_CONCAT(a.name, '||') as authors " +
                "FROM books b LEFT JOIN authors a ON b.gutenberg_id = a.book_id " +
                "GROUP BY b.gutenberg_id ORDER BY b.title;";
        List<Book> list = new ArrayList<>();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                Book b = new Book();
                b.setGutenbergId(rs.getInt("gutenberg_id"));
                b.setTitle(rs.getString("title"));
                b.setLanguage(rs.getString("language"));
                String auths = rs.getString("authors");
                List<Author> authors = new ArrayList<>();
                if (auths != null) {
                    for (String s : auths.split("\\|\\|")) {
                        Author a = new Author();
                        a.setName(s);
                        authors.add(a);
                    }
                }
                b.setAuthors(authors);
                list.add(b);
            }
        }
        return list;
    }

    public List<String> getAllAuthors() throws SQLException {
        Connection c = Database.getConnection();
        String q = "SELECT DISTINCT name FROM authors ORDER BY name;";
        List<String> list = new ArrayList<>();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        }
        return list;
    }

    public List<Book> getBooksByLanguage(String lang) throws SQLException {
        Connection c = Database.getConnection();
        String q = "SELECT b.gutenberg_id, b.title, b.language, GROUP_CONCAT(a.name, '||') as authors " +
                "FROM books b LEFT JOIN authors a ON b.gutenberg_id = a.book_id " +
                "WHERE b.language = ? GROUP BY b.gutenberg_id ORDER BY b.title;";
        List<Book> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, lang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setGutenbergId(rs.getInt("gutenberg_id"));
                    b.setTitle(rs.getString("title"));
                    b.setLanguage(rs.getString("language"));
                    String auths = rs.getString("authors");
                    List<Author> authors = new ArrayList<>();
                    if (auths != null) {
                        for (String s : auths.split("\\|\\|")) {
                            Author a = new Author();
                            a.setName(s);
                            authors.add(a);
                        }
                    }
                    b.setAuthors(authors);
                    list.add(b);
                }
            }
        }
        return list;
    }

    public void deleteAll() throws SQLException {
        Connection c = Database.getConnection();
        try (Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM authors;");
            st.executeUpdate("DELETE FROM books;");
        }
    }
}
