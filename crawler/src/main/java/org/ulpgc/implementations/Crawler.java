package org.ulpgc.implementations;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler implements org.ulpgc.model.Crawler {

    private static final String METADATA_FILE = "metadata.csv";
    private static final AtomicInteger customIdCounter = new AtomicInteger(1); // Start with ID 1

    public static int downloadBook(int bookId, String downloadDirectory) {
        String bookUrl = "https://www.gutenberg.org/files/" + bookId + "/" + bookId + "-0.txt";
        Path directoryPath = Paths.get(downloadDirectory);

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + downloadDirectory);
            return -1;
        }

        String[] titleAndAuthor = getTitleAndAuthor(bookId);
        if (titleAndAuthor == null || titleAndAuthor[0].isEmpty()) {
            System.err.println("Failed to get book title and author.");
            return -1;
        }

        String bookFileName = titleAndAuthor[0] + "_" + bookId + ".txt";
        Path filePath = directoryPath.resolve(bookFileName);

        try {
            URL url = new URL(bookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                }

                int customId = customIdCounter.getAndIncrement(); // Generate unique custom ID
                saveMetadata(customId, bookId, titleAndAuthor[0], titleAndAuthor[1], bookUrl);
                System.out.println("Download successful: " + bookFileName);
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("Book not found (404): " + bookId);
            } else {
                System.out.println("Error: HTTP response code " + responseCode);
            }

            return responseCode;

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return -1;
        }
    }

    public static String[] getTitleAndAuthor(int bookId) {
        String bookUrl = "https://www.gutenberg.org/ebooks/" + bookId;
        try {
            Document doc = Jsoup.connect(bookUrl).get();
            Element h1Element = doc.selectFirst("h1");

            if (h1Element != null) {
                String titleAndAuthor = h1Element.text();
                String[] parts = titleAndAuthor.split(" by ", 2); // Split by " by " to get title and author
                String title = parts[0];
                String author = parts.length > 1 ? parts[1] : "Unknown Author";
                return new String[] { title, author };
            } else {
                System.out.println("No h1 tag found on the page.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return null;
        }
    }

    private static void saveMetadata(int customId, int gutenbergId, String title, String author, String url) {
        String metadataEntry = customId + "," + gutenbergId + "," + title + "," + author + "," + url + "\n";
        try (FileWriter writer = new FileWriter(METADATA_FILE, true)) {
            writer.write(metadataEntry);
        } catch (IOException e) {
            System.err.println("Failed to write metadata: " + e.getMessage());
        }
    }
}




