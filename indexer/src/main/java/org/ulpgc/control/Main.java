package org.ulpgc.control;

import org.ulpgc.exceptions.IndexerException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IndexerException {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "BookDatalake"); // todo review
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        IndexerReader indexerReader = new GutenbergBookReader(bookDatalakePath.toString());

        IndexerStore hierarchicalCsvStore = new HierarchicalCsvStore(invertedIndexPath);
        // IndexerStore jsonIndexerStore = new JsonIndexerStore();
        IndexerController hierarchicalCsvController = new IndexerController(indexerReader, hierarchicalCsvStore);
        hierarchicalCsvController.execute();


        // IndexerController jsonIndexerController = new IndexerController(indexerReader,jsonIndexerStore);
        //TODO hierarchicalCsvController.execute()
        //TODO jsonIndexerController.execute()
        //TODO execute periodically
        //TODO execute parallel
    }
}
