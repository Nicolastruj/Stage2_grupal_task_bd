package org.ulpgc.indexer.control;
import org.ulpgc.indexer.model.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BooksReader {
    private String path;
    private List<Book> books;

    // Constructor
    public BooksReader(String path) {
        this.path = path;
        this.books = new ArrayList<>();
    }

    public void readBooks() {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (isTextFile(file)) {
                    Book book = createBookFromFileName(file.getName());
                    if (book != null) {
                        books.add(book);
                    }
                }
            }
        } else {
            System.out.println("La carpeta no contiene archivos o no se pudo acceder a ella.");
        }
    }

    // Método para verificar si el archivo es un archivo de texto
    private boolean isTextFile(File file) {
        return file.isFile() && file.getName().endsWith(".txt");
    }

    // Método para crear un objeto Book a partir del nombre del archivo
    private Book createBookFromFileName(String fileName) {
        String[] parts = fileName.split(" by ");
        if (parts.length == 2) {
            String title = parts[0];
            String authorAndIndex = parts[1].replace(".txt", "");
            String[] authorParts = authorAndIndex.split("_");

            if (authorParts.length == 2) {
                String author = authorParts[0];
                String index = authorParts[1];

                // Crear la URL del libro
                String bookId = index; // Aquí asumo que el índice es el mismo que book_id
                String url = "https://www.gutenberg.org/files/" + bookId + "/" + bookId + "-0.txt";

                // Crear y devolver un nuevo objeto Book
                return new Book(title, author, url, ""); // Se deja contenido vacío por ahora
            }
        }
        return null; // Retorna null si el formato no es correcto
    }

    // Getter para la lista de libros
    public List<Book> getBooks() {
        return books;
    }
}
