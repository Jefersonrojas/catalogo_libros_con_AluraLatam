import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LiterAlura - Catálogo de Libros ===");
        try {
            Database.init(); 
        } catch (Exception e) {
            System.err.println("Error iniciando la base de datos: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        Menu menu = new Menu(new Scanner(System.in));
        menu.run();
        Database.closeConnection();
        System.out.println("Saliendo. ¡Hasta luego!");
    }
}
