package org.ulpgc.control;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ulpgc.control.Crawler.downloadBook;


public class CrawlerController {
    public static int obtainLastId(String datamartPath) {
        int highestId = 0;

        Path directory = Paths.get(datamartPath);
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return highestId;
        }

        Pattern pattern = Pattern.compile("_(\\d+)\\.txt$");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.txt")) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                Matcher matcher = pattern.matcher(fileName);

                if (matcher.find()) {
                    int id = Integer.parseInt(matcher.group(1));
                    if (id > highestId) {
                        highestId = id;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while accessing the datamart: " + e.getMessage());
        }

        return highestId;
    }

    public static void downloadingProcess(String datamartPath) {
        int lastId = obtainLastId(datamartPath);
        int successfulDownloads = 0;

        while (successfulDownloads < 3) {
            int nextId = lastId + 1;
            lastId += 1;

            int status = downloadBook(nextId, datamartPath);

            if (status == 200) {
                successfulDownloads += 1;
            }
        }

        System.out.println("Three books downloaded successfully.");
    }

    public static void periodicTask(int interval, String datamartPath) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Starting download process...");
            downloadingProcess(datamartPath);
        }, 0, interval, TimeUnit.SECONDS);
    }
}