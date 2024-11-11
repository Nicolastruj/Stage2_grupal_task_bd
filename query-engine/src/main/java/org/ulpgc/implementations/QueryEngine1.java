package org.ulpgc.implementations;

import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.ports.IndexLoader;
import org.ulpgc.ports.MetadataLoader;
import org.ulpgc.model.Book;

import java.io.*;
import java.util.*;

public class QueryEngine1 {

    private final IndexLoader indexLoader;
    private final MetadataLoader metadataLoader;

    // Constructor to inject dependencies
    public QueryEngine1(IndexLoader indexLoader, MetadataLoader metadataLoader) {
        this.indexLoader = indexLoader;
        this.metadataLoader = metadataLoader;
    }


    public List<Map<String, Object>> query(String inputQuery, String indexFolder, String metadataPath,
                                           String bookFolder, int maxOccurrences) throws QueryEngineException, FileNotFoundException {

        List<Map<String, Object>> results = new ArrayList<>();
        String[] words = inputQuery.toLowerCase().split(" ");
        Map<String, Map<String, List<Integer>>> wordOccurrences = new HashMap<>();

        // Load metadata from CSV file
        Map<String, Book> metadataMap;
        try {
            metadataMap = metadataLoader.loadMetadata(metadataPath);
        } catch (IOException e) {
            throw new QueryEngineException(e.getMessage(),e);
        }

        // If the input is a single book ID, handle that separately
        if (inputQuery.matches("\\d+")) {
            String bookId = inputQuery.trim();
            Book metadata = metadataMap.get(bookId);
            if (metadata != null) {
                String bookPath = String.format("%s/%s_%s.txt", bookFolder, metadata.getName(), bookId);
                Map<String, Object> extractedData = new ParagraphExtractor().extractParagraphs(bookPath, Arrays.asList(words));

                List<String> paragraphs = (List<String>) extractedData.get("paragraphs");
                int occurrences = (int) extractedData.get("occurrences");

                if (!paragraphs.isEmpty()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("book_name", metadata.getName());
                    result.put("author_name", metadata.getAuthor());
                    result.put("URL", metadata.getUrl());
                    result.put("paragraphs", paragraphs);
                    result.put("total_occurrences", occurrences);
                    results.add(result);
                }
            } else {
                System.out.println("Book with ID '" + bookId + "' not found.");
            }
            return results;
        }

        // Load word indices from directory tree
        for (String word : words) {
            Map<String, List<Integer>> indexData;
            try {
                indexData = indexLoader.loadWord(word, indexFolder);
            } catch (IOException e) {
                throw new QueryEngineException(e.getMessage(),e);
            }
            if (indexData != null) {
                wordOccurrences.put(word, indexData);
            } else {
                System.out.println("Word '" + word + "' not found in any index.");
                return Collections.emptyList();
            }
        }

        // Find common books across all word indices
        Set<String> commonBooks = null;
        for (Map<String, List<Integer>> occurrences : wordOccurrences.values()) {
            if (commonBooks == null) {
                commonBooks = new HashSet<>(occurrences.keySet());
            } else {
                commonBooks.retainAll(occurrences.keySet());
            }
        }

        // Process each common book
        if (commonBooks != null) {
            for (String bookId : commonBooks) {
                Book metadata = metadataMap.get(bookId);
                if (metadata == null) {
                    System.out.println("Metadata for book ID '" + bookId + "' not found.");
                    continue;
                }

                String bookPath = String.format("%s/%s_%s.txt", bookFolder, metadata.getName(), bookId);
                Map<String, Object> extractedData = new ParagraphExtractor().extractParagraphs(bookPath, Arrays.asList(words));

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
    private static class ParagraphExtractor {
        public Map<String, Object> extractParagraphs(String bookPath, List<String> words) throws FileNotFoundException {
            Map<String, Object> result = new HashMap<>();
            List<String> paragraphs = new ArrayList<>();
            int totalOccurrences = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(bookPath))) {
                String paragraph;
                while ((paragraph = reader.readLine()) != null) {
                    int occurrences = countOccurrences(paragraph, words);
                    if (occurrences > 0) {
                        // Highlight the words in the paragraph
                        String highlightedParagraph = highlightWords(paragraph, words);
                        paragraphs.add(highlightedParagraph);  // Add each paragraph found
                        totalOccurrences += occurrences;
                    }
                }
            } catch (IOException e) {
                throw new FileNotFoundException("Error loading book content: " + e.getMessage());
            }

            result.put("paragraphs", paragraphs);  // Make sure all matching paragraphs are returned
            result.put("occurrences", totalOccurrences);  // Include total occurrences
            return result;
        }

        private int countOccurrences(String paragraph, List<String> words) {
            int occurrences = 0;
            String lowerParagraph = paragraph.toLowerCase();
            for (String word : words) {
                int index = lowerParagraph.indexOf(word);
                while (index != -1) {
                    occurrences++;
                    index = lowerParagraph.indexOf(word, index + 1);
                }
            }
            return occurrences;
        }

        private String highlightWords(String paragraph, List<String> words) {
            String highlightedParagraph = paragraph;
            // ANSI escape code for blue text
            String blueStart = "\033[34m";  // ANSI code for blue text
            String reset = "\033[0m";       // ANSI code to reset text formatting

            for (String word : words) {
                // Using a regular expression to match whole words, ignoring case
                String regex = "(?i)\\b" + word + "\\b";
                highlightedParagraph = highlightedParagraph.replaceAll(regex, blueStart + "$0" + reset);
            }

            return highlightedParagraph;
        }
    }
}
