package org.ulpgc.control;

import org.ulpgc.exceptions.QueryException;
import org.ulpgc.model.Book;
import org.ulpgc.ports.QueryReader;
import org.ulpgc.ports.QueryStore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class QueryCommand {
    private final QueryReader indexerReader;
    private final QueryStore indexerStore;

    public QueryCommand(QueryReader indexerReader, QueryStore indexerStore) {
        this.indexerReader = indexerReader;
        this.indexerStore = indexerStore;
    }

    public void execute() throws QueryException {
        Path bookPath = Paths.get(indexerReader.getPath());
        Path tempTray = Paths.get(System.getProperty("user.dir"), "tempTray");
        try {
            if (!Files.exists(tempTray)) {
                Files.createDirectories(tempTray);
            }
            copyLatestBooksToTempTray(bookPath, tempTray, 5);
        } catch (IOException e) {
            throw new QueryException("Error al mover los libros al tray", e);
        }
        String trayPath = tempTray.toString();
        List<Book> books = indexerReader.read(trayPath);

        for (Book book : books) {
            indexerStore.index(book);
        }
        try {
            // Si la tray es un directorio, primero elimina todos los archivos dentro
            if (Files.isDirectory(tempTray)) {
                try (Stream<Path> files = Files.walk(tempTray)) {
                    files.sorted(Comparator.reverseOrder())  // Eliminar primero los archivos más profundos
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    System.err.println("Error deleting file: " + path);
                                }
                            });
                } catch (IOException e) {
                    throw new QueryException("No se pudo recorrer el directorio para eliminar los archivos", e);
                }
            }

            // Ahora elimina el directorio o archivo vacío
            Files.deleteIfExists(tempTray);
            System.out.println("Temporary tray deleted: " + tempTray);
        } catch (IOException e) {
            throw new QueryException("No se pudo eliminar la tray", e);
        }
    }

    private void copyLatestBooksToTempTray(Path bookPath, Path tempTray, int n) throws IOException {
        try (Stream<Path> paths = Files.walk(bookPath)) {
            paths.filter(Files::isRegularFile)  // Filtramos solo archivos (libros)
                    .sorted((p1, p2) -> {
                        try {
                            return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .limit(n)  // Limitamos a los n libros más recientes
                    .forEach(sourcePath -> {
                        try {
                            // Obtener el nombre del archivo y la ruta destino
                            Path destinationPath = tempTray.resolve(sourcePath.getFileName());

                            // Copiar el archivo al directorio temporal
                            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
