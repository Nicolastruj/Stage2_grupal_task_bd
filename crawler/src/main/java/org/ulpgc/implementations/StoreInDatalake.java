package org.ulpgc.implementations;

import org.ulpgc.exceptions.CrawlerException;
import org.ulpgc.ports.StoreInDatalakeInterface;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreInDatalake implements StoreInDatalakeInterface {

    private static final String METADATA_FILE = "metadata.csv";
    private static final AtomicInteger customIdCounter = new AtomicInteger(loadLastCustomId() + 1);

    @Override
    public int saveBook(InputStream bookStream, String title, String downloadDirectory) throws CrawlerException {
        int customId = customIdCounter.getAndIncrement();
        String bookFileName = title + "_" + customId + ".txt";
        Path filePath = Paths.get(downloadDirectory, bookFileName);

        try {
            Files.createDirectories(filePath.getParent());
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bookStream.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new CrawlerException("Failed to save book: " + e.getMessage(), e);
        }

        return customId;
    }

    @Override
    public void saveMetadata(int customId, int gutenbergId, String title, String author, String url) throws CrawlerException {
        String metadataEntry = customId + "," + gutenbergId + "," + title + "," + author + "," + url + "\n";

        try (FileWriter writer = new FileWriter(METADATA_FILE, true)) {
            writer.write(metadataEntry);
        } catch (IOException e) {
            throw new CrawlerException("Failed to write metadata: " + e.getMessage(), e);
        }
    }

    private static int loadLastCustomId() {
        int lastId = 0;
        Path path = Paths.get(METADATA_FILE);

        if (Files.exists(path)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(METADATA_FILE))) {
                String line;
                String lastLine = null;

                while ((line = reader.readLine()) != null) {
                    lastLine = line;
                }

                if (lastLine != null) {
                    String[] fields = lastLine.split(",");
                    lastId = Integer.parseInt(fields[0].trim());
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to read last custom ID: " + e.getMessage());
            }
        }
        return lastId;
    }
}


