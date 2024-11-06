package org.ulpgc.control;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

public class IndexerController {
    private final IndexerReader indexerReader;
    private final IndexerStore indexerStore;

    public IndexerController(IndexerReader indexerReader, IndexerStore indexerStore) {
        this.indexerReader = indexerReader;
        this.indexerStore = indexerStore;
    }

    public void execute() throws IndexerException {
        Path bookPath = Paths.get(System.getProperty("user.dir"), indexerReader.getPath());
        Path tempTray = Paths.get(System.getProperty("user.dir"), "tempTray");
        try {
            if (!Files.exists(tempTray)) {
                Files.createDirectories(tempTray);
            }
            copyLatestBooksToTempTray(bookPath, tempTray, 5);
        } catch (IOException e) {
            throw new IndexerException("Error al mover los libros al tray", e);
        }
        String trayPath = tempTray.toString();
        List<Book> books = indexerReader.read(trayPath);

        for (Book book : books) {
            indexerStore.index(book);
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
