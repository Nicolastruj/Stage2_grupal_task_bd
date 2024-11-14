package org.ulpgc.control;

import org.ulpgc.exceptions.CrawlerException;
import org.ulpgc.implementations.ReaderFromWeb;
import org.ulpgc.implementations.StoreInDatalake;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrawlerController {

    public static int obtainLastId(String metadataPath) {
        int lastGutenbergId = 0;
        Path metadataFile = Paths.get(metadataPath);

        if (!Files.exists(metadataFile) || Files.isDirectory(metadataFile)) {
            return lastGutenbergId;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(metadataFile.toFile()))) {
            String line;
            String lastLine = null;

            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }

            if (lastLine != null) {
                String[] fields = lastLine.split(",");
                if (fields.length > 1) {
                    lastGutenbergId = Integer.parseInt(fields[1].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to read last Gutenberg ID: " + e.getMessage());
        }

        return lastGutenbergId;
    }

    public static void downloadingProcess(String datamartPath, String metadataPath) {
        int lastId = obtainLastId(metadataPath);
        int successfulDownloads = 0;

        // Instantiate ReaderFromWeb and StoreInDatalake to use non-static methods
        ReaderFromWeb reader = new ReaderFromWeb();
        StoreInDatalake store = new StoreInDatalake();

        while (successfulDownloads < 3) {
            int nextId = lastId + 1;
            lastId += 1;

            try {
                String[] titleAndAuthor = reader.getTitleAndAuthor(nextId);

                if (titleAndAuthor != null) {
                    try (InputStream bookStream = reader.downloadBookStream(nextId)) {
                        if (bookStream != null) {
                            int customId = store.saveBook(bookStream, titleAndAuthor[0], datamartPath);
                            store.saveMetadata(customId, nextId, titleAndAuthor[0], titleAndAuthor[1],
                                    "https://www.gutenberg.org/files/" + nextId + "/" + nextId + "-0.txt");
                            successfulDownloads++;
                            System.out.println("Successfully downloaded book ID " + nextId);
                        } else {
                            System.out.println("Book not found: " + nextId); // Print as normal message
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("Failed to retrieve title and author for book ID " + nextId);
                }
            } catch (CrawlerException e) {
                System.err.println("Error: " + e.getMessage());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Three books downloaded successfully.");
    }

    public static void periodicTask(int interval, String datalakePath, String metadataPath) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Starting download process...");
            downloadingProcess(datalakePath, metadataPath);
        }, 0, interval, TimeUnit.SECONDS);
    }
}



