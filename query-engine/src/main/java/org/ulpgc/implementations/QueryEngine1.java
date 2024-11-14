package org.ulpgc.implementations;

import org.ulpgc.exceptions.QueryException;
import org.ulpgc.ports.IndexLoader;
import org.ulpgc.ports.MetadataLoader;
import org.ulpgc.model.Book;

import java.io.*;
import java.util.*;

public class QueryEngine1 {

    private final IndexLoader indexLoader;
    private final MetadataLoader metadataLoader;

    public QueryEngine1(IndexLoader indexLoader, MetadataLoader metadataLoader) {
        this.indexLoader = indexLoader;
        this.metadataLoader = metadataLoader;
    }


    public List<Map<String, Object>> query(String inputQuery, String indexFolder, String metadataPath,
                                           String bookFolder) throws QueryException{

        List<Map<String, Object>> results = new ArrayList<>();
        String[] words = inputQuery.toLowerCase().split(" ");
        Map<String, Map<String, List<Integer>>> wordOccurrences = new HashMap<>();

        // Load metadata from CSV file
        Map<String, Book> metadataMap;
        try {
            metadataMap = metadataLoader.loadMetadata(metadataPath);
        } catch (IOException e) {
            throw new QueryException(e.getMessage(), e);
        }

        // Load word indices from directory tree for words in the query
        for (String word : words) {
            Map<String, List<Integer>> indexData;
            try {
                indexData = indexLoader.loadWord(word, indexFolder);
            } catch (IOException e) {
                throw new QueryException(e.getMessage(), e);
            }
            if (indexData != null) {
                wordOccurrences.put(word, indexData);
            } else {
                return Collections.emptyList();
            }
        }

        // Find common books
        Set<String> booksInCommon = null;
        for (Map<String, List<Integer>> occurrences : wordOccurrences.values()) {
            if (booksInCommon == null) {
                booksInCommon = new HashSet<>(occurrences.keySet());
            } else {
                booksInCommon.retainAll(occurrences.keySet());
            }
        }

        // Process each common book and save the data and paragraphs
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
        }
        return results;
    }

    private static class ParagraphExtractor {
        public Map<String, Object> findParagraphs(String bookPath, List<String> words) throws QueryException {
            Map<String, Object> result = new HashMap<>();
            List<String> paragraphs = new ArrayList<>();
            int totalOccurrences = 0;

            // Read the book file per paragraph
            StringBuilder paragraphBuilder = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(bookPath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Add the current line to the paragraph
                    paragraphBuilder.append(line).append("\n");

                    // Check if the paragraph ends
                    if (line.trim().isEmpty()) {
                        // Process the full paragraph
                        String paragraph = paragraphBuilder.toString().trim();
                        if (!paragraph.isEmpty()) {
                            int occurrences = countOccurrences(paragraph, words);
                            if (occurrences > 0) {
                                // Highlight the words
                                String highlightedParagraph = highlightWords(paragraph, words);
                                paragraphs.add(highlightedParagraph);
                                totalOccurrences += occurrences;
                            }
                        }
                        // Reset for checking next paragraph
                        paragraphBuilder.setLength(0);
                    }
                }

                // Case where last paragraph is not followed by a blank line
                if (paragraphBuilder.length() > 0) {
                    String paragraph = paragraphBuilder.toString().trim();
                    if (!paragraph.isEmpty()) {
                        int occurrences = countOccurrences(paragraph, words);
                        if (occurrences > 0) {
                            String highlightedParagraph = highlightWords(paragraph, words);
                            paragraphs.add(highlightedParagraph);
                            totalOccurrences += occurrences;
                        }
                    }
                }

            } catch (IOException e) {
                throw new QueryException(e.getMessage(), e);
            }

            result.put("paragraphs", paragraphs);
            result.put("occurrences", totalOccurrences);
            return result;
        }

        private int countOccurrences(String paragraph, List<String> words) {
            int occurrences = 0;
            String lowerParagraph = paragraph.toLowerCase();
            for (String word : words) {
                // Match the word as a whole word, not as part of other words
                String regex = "(?i)\\b" + word + "\\b";
                occurrences += (lowerParagraph.length() - lowerParagraph.replaceAll(regex, "").length()) / word.length();
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
