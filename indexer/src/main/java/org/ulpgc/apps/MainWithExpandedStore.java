package org.ulpgc.apps;

import org.ulpgc.control.IndexerCommand;
import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.implementations.ExpandedHierarchicalCsvStore;
import org.ulpgc.implementations.GutenbergBookReader;
import org.ulpgc.ports.IndexerReader;
import org.ulpgc.ports.IndexerStore;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainWithExpandedStore {
    public static void main(String[] args) throws IndexerException {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        Path stopWordsPath;
        try {
            stopWordsPath = Paths.get(MainWithAggregatedStore.class.getClassLoader()
                    .getResource("stopwords.txt").toURI());
        } catch (URISyntaxException e) {
            throw new IndexerException(e.getMessage(), e);
        }
        IndexerReader indexerReader = new GutenbergBookReader(bookDatalakePath.toString());

        IndexerStore hierarchicalCsvStore = new ExpandedHierarchicalCsvStore(invertedIndexPath, stopWordsPath);
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