package org.ulpgc.implementations;

import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.model.Book;
import org.ulpgc.ports.IndexLoader;
import org.ulpgc.ports.MetadataLoader;

import java.io.IOException;
import java.util.*;

public class QueryEngineExpanded {
    private final IndexLoader indexLoader;
    private final MetadataLoader metadataLoader;

    public QueryEngineExpanded(IndexLoader indexLoader, MetadataLoader metadataLoader) {
        this.indexLoader = indexLoader;
        this.metadataLoader = metadataLoader;
    }

    public List<Map<String, Object>> query(String inputQuery, String indexFolder, String metadataPath, String bookFolder) throws QueryEngineException {
        List<Map<String, Object>> results = new ArrayList<>();
        String[] words = inputQuery.toLowerCase().split(" ");
        Map<String, Map<String, List<Integer>>> wordOccurrences = new HashMap<>();
        Map<String, Book> metadataMap;

        try {
            metadataMap = metadataLoader.loadMetadata(metadataPath);
        } catch (IOException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }

        List<Map<String, Object>> emptyList = loadWordIndices(indexFolder, words, wordOccurrences);
        if (emptyList != null) return emptyList;

        Set<String> booksInCommon = findCommonBooks(wordOccurrences);
        saveFoundData(bookFolder, booksInCommon, metadataMap, words, results);

        return results;
    }

    private List<Map<String, Object>> loadWordIndices(String indexFolder, String[] words, Map<String, Map<String, List<Integer>>> wordOccurrences) throws QueryEngineException {
        for (String word : words) {
            Map<String, List<Integer>> indexData;
            try {
                indexData = indexLoader.loadWord(word, indexFolder);
            } catch (IOException e) {
                throw new QueryEngineException(e.getMessage(), e);
            }
            if (indexData != null) {
                wordOccurrences.put(word, indexData);
            } else {
                return Collections.emptyList();
            }
        }
        return null;
    }

    private static Set<String> findCommonBooks(Map<String, Map<String, List<Integer>>> wordOccurrences) {
        Set<String> booksInCommon = null;
        for (Map<String, List<Integer>> occurrences : wordOccurrences.values()) {
            if (booksInCommon == null) {
                booksInCommon = new HashSet<>(occurrences.keySet());
            } else {
                booksInCommon.retainAll(occurrences.keySet());
            }
        }
        return booksInCommon;
    }

    private static void saveFoundData(String bookFolder, Set<String> booksInCommon, Map<String, Book> metadataMap, String[] words, List<Map<String, Object>> results) throws QueryEngineException {
        if (booksInCommon != null) {
            for (String bookId : booksInCommon) {
                Book metadata = metadataMap.get(bookId);
                if (metadata == null) {
                    System.out.println("Metadata for book ID '" + bookId + "' not found.");
                    continue;
                }
                String bookPath = String.format("%s/%s_%s.txt", bookFolder, metadata.getName(), bookId);
                Map<String, Object> extractedData = new ParagraphExtractor().findParagraphs(bookPath, Arrays.asList(words));
                List<String> paragraphs = (List<String>) extractedData.get("paragraphs");
                int occurrences = (int) extractedData.get("occurrences");
                saveResult(results, paragraphs, metadata, occurrences);
            }
        }
    }

    private static void saveResult(List<Map<String, Object>> results, List<String> paragraphs, Book metadata, int occurrences) {
        if (!paragraphs.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("book_name", metadata.getName());
            result.put("author_name", metadata.getAuthor());
            result.put("URL", metadata.getUrl());
            result.put("paragraphs", paragraphs);
            result.put("total_occurrences", occurrences);
            results.add(result);
        }
    }

    private static class ParagraphExtractor {
        public Map<String, Object> findParagraphs(String bookPath, List<String> words) throws QueryEngineException {
            Map<String, Object> result = new HashMap<>();
            List<String> paragraphs = new ArrayList<>();
            int totalOccurrences = 0;

            // Logic to extract paragraphs matching words (similar to the original `QueryEngineAggregated`)
            
            result.put("paragraphs", paragraphs);
            result.put("occurrences", totalOccurrences);
            return result;
        }
    }
}
