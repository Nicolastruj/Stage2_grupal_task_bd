package org.ulpgc.apps;

import org.ulpgc.control.Command;
import org.ulpgc.control.CrawlerCommand;
import org.ulpgc.implementations.ReaderFromWeb;
import org.ulpgc.implementations.StoreInDatalake;
import org.ulpgc.ports.ReaderFromWebInterface;
import org.ulpgc.ports.StoreInDatalakeInterface;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class Main {
    public static void main(String[] args) {
        Path datalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "metadata.csv");

        ReaderFromWebInterface reader = new ReaderFromWeb();
        StoreInDatalakeInterface store = new StoreInDatalake(metadataPath.toString());
        Command crawlerCommand = new CrawlerCommand(datalakePath.toString(), metadataPath.toString(), reader, store);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        periodicTask(scheduler, crawlerCommand);
    }

    private static void periodicTask(ScheduledExecutorService scheduler, Command crawlerCommand) {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Starting download process...");
            crawlerCommand.download();
        }, 0, 20, TimeUnit.SECONDS);
    }

}
