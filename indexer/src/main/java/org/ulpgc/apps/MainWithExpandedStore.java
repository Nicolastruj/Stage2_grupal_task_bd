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
    public static void main(String[] args) {
        Path datalakePath = Paths.get(System.getProperty("user.dir"), "/data/datalake").normalize();
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "/data/datamart").normalize();
        // TODO hacerlo con listas si no lo sacan con fichero
        Path stopWordsPath = Paths.get(System.getProperty("user.dir"), "/data/stopwords/stopwords.txt");
        IndexerReader indexerReader = new GutenbergBookReader(datalakePath.toString());

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