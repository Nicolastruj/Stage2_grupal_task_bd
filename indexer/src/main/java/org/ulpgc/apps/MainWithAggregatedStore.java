package org.ulpgc.apps;

import org.ulpgc.control.IndexerCommand;
import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.implementations.AggregatedHierarchicalCsvStore;
import org.ulpgc.implementations.GutenbergBookReader;
import org.ulpgc.ports.IndexerReader;
import org.ulpgc.ports.IndexerStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainWithAggregatedStore {
    public static void main(String[] args) {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "/data/datalake");
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "/data/datamart");
        IndexerReader indexerReader = new GutenbergBookReader(bookDatalakePath.toString());

        IndexerStore hierarchicalCsvStore = new AggregatedHierarchicalCsvStore(invertedIndexPath);
        IndexerCommand hierarchicalCsvController = new IndexerCommand(indexerReader, hierarchicalCsvStore);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                hierarchicalCsvController.execute();
            } catch (IndexerException e) {
                throw new RuntimeException("Error while indexing books.", e);
            }
        }, 0, 10, TimeUnit.MINUTES);
    }
}