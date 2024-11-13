package org.ulpgc.apps;

import org.ulpgc.control.QueryCommand;
import org.ulpgc.implementations.*;
import org.ulpgc.exceptions.QueryException;
import org.ulpgc.ports.QueryReader;
import org.ulpgc.ports.QueryStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws QueryException {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake"); // todo review
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        QueryReader indexerReader = new GutenbergBookReader(bookDatalakePath.toString());

        QueryStore hierarchicalCsvStore = new HierarchicalCsvStore(invertedIndexPath);
        QueryCommand hierarchicalCsvController = new QueryCommand(indexerReader, hierarchicalCsvStore);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                hierarchicalCsvController.execute();
            } catch (QueryException e) {
                throw new RuntimeException("Fallo al ejecutar la indexacion",e);
            }
        }, 0, 10, TimeUnit.MINUTES);
        //TODO hierarchicalCsvController.execute()
        //TODO jsonQueryController.execute()
        //TODO execute parallel
        //TODO hacer que el reader ponga como id el que esta al final del nombre del archivo
    }
}
