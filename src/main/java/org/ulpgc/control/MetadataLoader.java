package org.ulpgc.control;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ulpgc.model.Book;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class MetadataLoader {

    public Book loadMetadata(String bookId, String metadataFolder) {
        int hundredRange = (Integer.parseInt(bookId) / 100) * 100;
        String jsonFilename = "books_metadata_" + hundredRange + "-" + (hundredRange + 99) + ".json";
        String jsonFilePath = metadataFolder + "/" + jsonFilename;

        try {
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray booksData = new JSONArray(content);

            for (int i = 0; i < booksData.length(); i++) {
                JSONObject bookJson = booksData.getJSONObject(i);
                if (bookJson.getString("id_book").equals(bookId)) {
                    return new Book(bookId, bookJson.getString("book_name"), 
                            bookJson.getString("author"), bookJson.getString("URL"));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading metadata file: " + e.getMessage());
        }
        return null;
    }
}