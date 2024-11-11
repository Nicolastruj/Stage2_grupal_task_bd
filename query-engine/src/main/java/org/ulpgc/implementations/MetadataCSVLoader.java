package org.ulpgc.implementations;
import org.ulpgc.ports.MetadataLoader;
import org.ulpgc.model.Book;

import java.io.*;
import java.util.*;

public class MetadataCSVLoader implements MetadataLoader{
    @Override
    public Map<String, Book> loadMetadata(String metadataPath) throws IOException {
        Map<String, Book> metadataMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(metadataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;  // Skip malformed lines
                String bookId = parts[1]; // Book ID should be in the second column
                String title = parts[2];
                String author = parts[3];
                String url = parts[4];
                metadataMap.put(bookId, new Book(bookId, title, author, url));
            }
        }

        return metadataMap;
    }
}
