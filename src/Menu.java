import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final BookRepository repo;
    private final ApiClient api;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        this.repo = new BookRepository();
        this.api = new ApiClient();
    }

    public void run() {
        while (true) {
            showOptions();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": searchAndSave(); break;
                case "2": listBooks(); break;
                case "3": listAuthors(); break;
                case "4": listByLanguage(); break;
                case "5": deleteAll(); break;
                case "6": return;
                default: System.out.println("Opción inválida, intenta de nuevo."); break;
            }
        }
    }

    private void showOptions() {
        System.out.println("\nOpciones:");
        System.out.println("1) Buscar libro por título (Gutendex) y guardar en DB");
        System.out.println("2) Listar libros guardados");
        System.out.println("3) Listar autores guardados");
        System.out.println("4) Listar libros por idioma");
        System.out.println("5) Eliminar todos los libros de la base de datos");
        System.out.println("6) Salir");
        System.out.print("Elige una opción: ");
    }

    private void searchAndSave() {
        System.out.print("Ingrese título (fracción) para buscar: ");
        String q = scanner.nextLine().trim();
        try {
            List<Book> found = api.searchByTitle(q);
            if (found.isEmpty()) {
                System.out.println("No se encontraron resultados.");
                return;
            }
            System.out.println("Resultados encontrados:");
            for (int i = 0; i < found.size(); i++) {
                Book b = found.get(i);
                System.out.printf("%d) %s — %s [%s]\n", i+1, b.getTitle(), b.getAuthorsAsString(), b.getLanguage());
            }
            System.out.print("Número del libro a guardar (0 para cancelar): ");
            String s = scanner.nextLine().trim();
            int idx = Integer.parseInt(s);
            if (idx <= 0 || idx > found.size()) {
                System.out.println("Operación cancelada.");
                return;
            }
            Book toSave = found.get(idx-1);
            repo.saveBook(toSave);
            System.out.println("Libro guardado en la base de datos.");
        } catch (Exception e) {
            System.err.println("Error durante búsqueda/guardado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listBooks() {
        try {
            List<Book> books = repo.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("No hay libros registrados.");
                return;
            }
            System.out.println("Libros registrados:");
            for (Book b : books) {
                System.out.printf("- %s | Autores: %s | Idioma: %s | Gutenberg ID: %d\n",
                        b.getTitle(), b.getAuthorsAsString(), b.getLanguage(), b.getGutenbergId());
            }
        } catch (Exception e) {
            System.err.println("Error listando libros: " + e.getMessage());
        }
    }

    private void listAuthors() {
        try {
            List<String> authors = repo.getAllAuthors();
            if (authors.isEmpty()) {
                System.out.println("No hay autores registrados.");
                return;
            }
            System.out.println("Autores registrados:");
            for (String a : authors) System.out.println("- " + a);
        } catch (Exception e) {
            System.err.println("Error listando autores: " + e.getMessage());
        }
    }

    private void listByLanguage() {
        System.out.print("Ingrese el código de idioma (ejemplo: en, es, fr): ");
        String lang = scanner.nextLine().trim();
        try {
            List<Book> books = repo.getBooksByLanguage(lang);
            if (books.isEmpty()) {
                System.out.println("No se encontraron libros para el idioma: " + lang);
                return;
            }
            System.out.println("Libros en idioma " + lang + ":");
            for (Book b : books) {
                System.out.printf("- %s | Autores: %s | ID: %d\n", b.getTitle(), b.getAuthorsAsString(), b.getGutenbergId());
            }
        } catch (Exception e) {
            System.err.println("Error buscando por idioma: " + e.getMessage());
        }
    }

    private void deleteAll() {
        System.out.print("¿Estás seguro? Escribe 'SI' para confirmar: ");
        String c = scanner.nextLine().trim();
        if (!"SI".equalsIgnoreCase(c)) {
            System.out.println("Cancelado.");
            return;
        }
        try {
            repo.deleteAll();
            System.out.println("Todos los libros fueron eliminados.");
        } catch (Exception e) {
            System.err.println("Error eliminando registros: " + e.getMessage());
        }
    }
}
