package org.ulpgc.apps;

import org.ulpgc.control.QueryEngine;
import org.ulpgc.control.MetadataLoader;
import org.ulpgc.control.ParagraphExtractor;
import org.ulpgc.ports.IndexerReader;
import org.ulpgc.ports.IndexerStore;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Initialize required components
        IndexerReader indexerReader = new JsonIndexLoader(); // Replace with the appropriate implementation
        IndexerStore indexerStore = new IndexerStoreImpl(); // Replace with your IndexerStore implementation
        MetadataLoader metadataLoader = new MetadataLoader();
        ParagraphExtractor paragraphExtractor = new ParagraphExtractor();

        // Initialize QueryEngine
        QueryEngine queryEngine = new QueryEngine(indexerReader, indexerStore, metadataLoader, paragraphExtractor);

        // Sample query execution
        String inputQuery = "example query"; // Replace with desired query
        String indexFolder = "path/to/indexFolder"; // Replace with the actual path to index folder
        String metadataFolder = "path/to/metadataFolder"; // Replace with the actual path to metadata folder
        String bookFolder = "path/to/bookFolder"; // Replace with the actual path to book folder
        int maxOccurrences = 5; // Number of occurrences to display

        // Run the query
        List<Map<String, Object>> results = queryEngine.query(inputQuery, indexFolder, metadataFolder, bookFolder, maxOccurrences);

        // Display results
        for (Map<String, Object> result : results) {
            System.out.println(result);
        }
    }
}