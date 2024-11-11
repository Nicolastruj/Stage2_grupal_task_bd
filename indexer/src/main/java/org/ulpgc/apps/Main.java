package org.ulpgc.apps;

import org.ulpgc.control.IndexerCommand;
import org.ulpgc.implementations.*;
import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.ports.IndexerReader;
import org.ulpgc.ports.IndexerStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IndexerException {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake"); // todo review
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        IndexerReader indexerReader = new GutenbergBookReader(bookDatalakePath.toString());

        IndexerStore hierarchicalCsvStore = new HierarchicalCsvStore(invertedIndexPath);
        IndexerCommand hierarchicalCsvController = new IndexerCommand(indexerReader, hierarchicalCsvStore);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                hierarchicalCsvController.execute();
            } catch (IndexerException e) {
                throw new RuntimeException("Fallo al ejecutar la indexacion",e);
            }
        }, 0, 10, TimeUnit.MINUTES);
        //TODO hierarchicalCsvController.execute()
        //TODO jsonIndexerController.execute()
        //TODO execute parallel
        //TODO hacer que el reader ponga como id el que esta al final del nombre del archivo
    }
}