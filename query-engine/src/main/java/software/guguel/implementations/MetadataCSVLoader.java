package software.guguel.implementations;
import software.guguel.exceptions.QueryEngineException;
import software.guguel.ports.MetadataLoader;
import software.guguel.model.Book;

import java.io.*;
import java.util.*;

public class MetadataCSVLoader implements MetadataLoader{
    @Override
    public Map<String, Book> loadMetadata(String metadataPath) throws QueryEngineException {
        Map<String, Book> metadata = new HashMap<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(metadataPath));
        } catch (FileNotFoundException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }
        String line;
            while (true) {
                try {
                    if ((line = reader.readLine()) == null) break;
                } catch (IOException e) {
                    throw new QueryEngineException(e.getMessage(), e);
                }
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                String bookId = parts[1]; // Book ID should be in the second column (?)
                String title = parts[2];
                String author = parts[3];
                String url = parts[4];
                metadata.put(bookId, new Book(bookId, title, author, url));
            }

        return metadata;
    }
}
