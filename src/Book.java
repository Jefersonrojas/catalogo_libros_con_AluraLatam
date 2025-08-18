import java.util.List;
import java.util.stream.Collectors;

public class Book {
    private int gutenbergId;
    private String title;
    private String language;
    private List<Author> authors;

    public Book() {}

    public int getGutenbergId() { return gutenbergId; }
    public void setGutenbergId(int id) { this.gutenbergId = id; }

    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }

    public String getLanguage() { return language; }
    public void setLanguage(String l) { this.language = l; }

    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { this.authors = authors; }

    public String getAuthorsAsString() {
        if (authors == null || authors.isEmpty()) return "Desconocido";
        return authors.stream().map(Author::getName).collect(Collectors.joining(", "));
    }
}
