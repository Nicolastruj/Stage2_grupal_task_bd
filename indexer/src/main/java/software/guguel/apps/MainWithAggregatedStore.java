package software.guguel.apps;

import software.guguel.control.IndexerCommand;
import software.guguel.exceptions.IndexerException;
import software.guguel.implementations.AggregatedHierarchicalCsvStore;
import software.guguel.implementations.GutenbergBookReader;
import software.guguel.ports.IndexerReader;
import software.guguel.ports.IndexerStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainWithAggregatedStore {
    public static void main(String[] args) {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
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