package org.ulpgc.apps;

import org.ulpgc.implementations.GutenbergBookReader;
import org.ulpgc.implementations.HierarchicalCsvStore;
import org.ulpgc.control.IndexerCommand;
import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.ports.IndexerReader;
import org.ulpgc.ports.IndexerStore;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MainWithCsvStructure {
    public static void main(String[] args) throws IndexerException {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake"); // todo review
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        IndexerReader indexerReader = new GutenbergBookReader(bookDatalakePath.toString());

        IndexerStore hierarchicalCsvStore = new HierarchicalCsvStore(invertedIndexPath);
        // IndexerStore jsonIndexerStore = new JsonIndexerStore();
        IndexerCommand hierarchicalCsvController = new IndexerCommand(indexerReader, hierarchicalCsvStore);
        hierarchicalCsvController.execute();


        // IndexerCommand jsonIndexerController = new IndexerCommand(indexerReader,jsonIndexerStore);
        //TODO hierarchicalCsvController.execute()
        //TODO jsonIndexerController.execute()
    }
}
