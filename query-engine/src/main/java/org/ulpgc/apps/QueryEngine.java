package org.ulpgc.control;

import java.util.*;

import org.ulpgc.model.Book;

import org.json.JSONObject;
import org.ulpgc.implementations.MetadataCSVLoader;
import org.ulpgc.implementations.MetadataLoader;
import org.ulpgc.ports.QueryReader;
import org.ulpgc.ports.QueryStore;




import java.util.List;
import java.util.Map;




public class QueryEngine {


    private final QueryReader indexerReader;Extractor paragraphExtractor;

    private MetadataLoader metadataLoader;
    public QueryEngine(MetadataLoader metadataLoader) {
        this.metadataLoader = metadataLoader;
    }


    public QueryEngine(QueryReader indexerReader, QueryStore indexerStore, MetadataLoader metadataLoader, ParagraphExtractor paragraphExtractor) {
        this.indexerReader = indexerReader;
        this.indexerStore = indexerStore;
        this.metadataLoader = metadataLoader;
        this.paragraphExtractor = paragraphExtractor;
    }

    public List<Map<String, Object>> query(String inputQuery, String indexFolder, String metadataFolder, 
                                           String bookFolder, int maxOccurrences) {
        List<Map<String, Object>> results = new ArrayList<>();
        String[] words = inputQuery.toLowerCase().split(" ");
        Map<String, JSONObject> wordOccurrences = new HashMap<>();

        // Load word indices
        for (String word : words) {
            JSONObject indexData = indexerReader.loadJsonIndex(word, indexFolder);
            if (indexData.has(word)) {
                wordOccurrences.put(word, indexData.getJSONObject(word));
            } else {
                System.out.println("Word '" + word + "' not found in any index.");
                return Collections.emptyList();
            }
        }

        // Find common books
        Set<String> commonBooks = findCommonBooks(wordOccurrences);

        if (commonBooks != null) {
            for (String bookId : commonBooks) {
                Book metadata = metadataLoader.loadMetadata(bookId, metadataFolder);
                if (metadata == null) {
                    System.out.println("Metadata for book ID '" + bookId + "' not found.");
                    continue;
                }

                String bookPath = bookFolder + "/" + metadata.getName() + " by " + metadata.getAuthor() + "_" + bookId + ".txt";
                Map<String, Object> extractedData = paragraphExtractor.extractParagraphs(bookPath, Arrays.asList(words));

                List<String> paragraphs = (List<String>) extractedData.get("paragraphs");
                int occurrences = (int) extractedData.get("occurrences");

                if (!paragraphs.isEmpty()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("book_name", metadata.getName());
                    result.put("author_name", metadata.getAuthor());
                    result.put("URL", metadata.getUrl());
                    result.put("paragraphs", paragraphs.subList(0, Math.min(maxOccurrences, paragraphs.size())));
                    result.put("total_occurrences", occurrences);
                    results.add(result);
                }
            }
        }
        return results;
    }

    private Set<String> findCommonBooks(Map<String, JSONObject> wordOccurrences) {
        Set<String> commonBooks = null;
        for (JSONObject occurrences : wordOccurrences.values()) {
            if (commonBooks == null) {
                commonBooks = occurrences.keySet();
            } else {
                commonBooks.retainAll(occurrences.keySet());
            }
        }
        return commonBooks;
    }
}
