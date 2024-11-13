// package org.ulpgc.apps;

// import org.ulpgc.control.QueryEngine;
// import org.ulpgc.control.MetadataLoader;
// import org.ulpgc.control.ParagraphExtractor;
// import org.ulpgc.ports.IndexerReader;
// import org.ulpgc.ports.IndexerStore;

// import java.util.List;
// import java.util.Map;

// public class Main {
//     public static void main(String[] args) {
//         // Initialize required components
//         IndexerReader indexerReader = new JsonIndexLoader(); // Replace with the appropriate implementation
//         IndexerStore indexerStore = new IndexerStoreImpl(); // Replace with your IndexerStore implementation
//         MetadataLoader metadataLoader = new MetadataLoader();
//         ParagraphExtractor paragraphExtractor = new ParagraphExtractor();

//         // Initialize QueryEngine
//         QueryEngine queryEngine = new QueryEngine(indexerReader, indexerStore, metadataLoader, paragraphExtractor);

//         // Sample query execution
//         String inputQuery = "hello"; 
//         String indexFolder = "path/to/indexFolder"; 
//         String metadataFolder = "path/to/metadataFolder"; // Replace with the actual path to metadata folder
//         String bookFolder = "path/to/bookFolder"; // Replace with the actual path to book folder
//         int maxOccurrences = 5; // Number of occurrences to display

//         // Run the query
//         List<Map<String, Object>> results = queryEngine.query(inputQuery, indexFolder, metadataFolder, bookFolder, maxOccurrences);

//         // Display results
//         for (Map<String, Object> result : results) {
//             System.out.println(result);
//         }
//     }
// }



package org.ulpgc.apps;

import org.ulpgc.control.QueryEngine;
import org.ulpgc.implementations.MetadataCSVLoader;
import org.ulpgc.ports.MetadataLoader;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String metadataPath = "../../metadata.csv";
        String indexFolder = "../../indexer/target/classes/org/ulpgc";  // Adjust this if "indexFolder" refers to another specific path.
        String bookFolder = "../../BookDatalake";

        
        int maxOccurrences = 5;
        String inputQuery = "your search query here";

        MetadataLoader metadataLoader = new MetadataCSVLoader();
        QueryEngine queryEngine = new QueryEngine(metadataLoader);

        List<Map<String, Object>> results = queryEngine.query(inputQuery, indexFolder, metadataPath, bookFolder, maxOccurrences);

        // Print results
        for (Map<String, Object> result : results) {
            System.out.println("Book: " + result.get("book_name"));
            System.out.println("Author: " + result.get("author_name"));
            System.out.println("URL: " + result.get("URL"));
            System.out.println("Paragraphs: " + result.get("paragraphs"));
            System.out.println("Total Occurrences: " + result.get("total_occurrences"));
            System.out.println("----------");
        }
    }
}