package org.ulpgc.control;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler {

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

        String bookFileName = getTitle(bookId);
        if (bookFileName == null || bookFileName.isEmpty()) {
            System.err.println("Failed to get book title.");
            return -1;
        }

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

    public static String getTitle(int bookId) {
        String bookUrl = "https://www.gutenberg.org/ebooks/" + bookId;
        try {
            Document doc = Jsoup.connect(bookUrl).get();

            Element h1Element = doc.selectFirst("h1");

            if (h1Element != null) {
                String title = h1Element.text();
                return title + "_" + bookId + ".txt";
            } else {
                System.out.println("No h1 tag found on the page.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return null;
        }
    }
}