package org.ulpgc.implementations;

import org.ulpgc.exceptions.QueryException;
import org.ulpgc.model.Book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class MetadataCSVLoader implements MetadataLoader {
    @Override
    public Map<String, Book> loadMetadata(String metadataPath) throws QueryEngineException {
        Map<String, Book> metadata = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(metadataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String bookId = parts[1].trim();
                String title = parts[2].trim();
                String author = parts[3].trim();
                String url = parts[4].trim();

                metadata.put(bookId, new Book(bookId, title, author, url));
            }
        } catch (IOException e) {
            throw new QueryEngineException("Error reading metadata file: " + e.getMessage(), e);
        }
        return metadata;
    }
}