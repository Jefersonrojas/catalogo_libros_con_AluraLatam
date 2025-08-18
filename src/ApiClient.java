import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;

public class ApiClient {
    private static final String API = "https://gutendex.com/books?search=";

    public List<Book> searchByTitle(String title) throws Exception {
        String q = URLEncoder.encode(title, "UTF-8");
        URL url = new URL(API + q);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 400 ? con.getInputStream() : con.getErrorStream()
        ));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        JsonObject root = JsonParser.parseString(content.toString()).getAsJsonObject();
        JsonArray results = root.getAsJsonArray("results");
        List<Book> list = new ArrayList<>();
        Gson gson = new Gson();
        for (JsonElement el : results) {
            JsonObject obj = el.getAsJsonObject();
            Book b = new Book();
            b.setTitle(obj.has("title") ? obj.get("title").getAsString() : "Sin título");
            b.setGutenbergId(obj.has("id") ? obj.get("id").getAsInt() : -1);
            b.setLanguage(obj.has("languages") && obj.getAsJsonArray("languages").size() > 0 ? obj.getAsJsonArray("languages").get(0).getAsString() : "");
            // authors array
            List<Author> authors = new ArrayList<>();
            if (obj.has("authors")) {
                JsonArray aarr = obj.getAsJsonArray("authors");
                for (JsonElement ae : aarr) {
                    JsonObject ao = ae.getAsJsonObject();
                    Author a = new Author();
                    a.setName(ao.has("name") ? ao.get("name").getAsString() : "Anon");
                    authors.add(a);
                }
            }
            b.setAuthors(authors);
            list.add(b);
        }
        return list;
    }
}
