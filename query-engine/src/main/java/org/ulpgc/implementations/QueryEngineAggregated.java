package org.ulpgc.implementations;

import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.ports.MetadataLoader;
import org.ulpgc.model.Book;

import java.io.*;
import java.util.*;

public class QueryEngineAggregated {
    private final MetadataLoader metadataLoader;
    private final String indexFolder;

    public QueryEngineAggregated(MetadataLoader metadataLoader, String indexFolder) {
        this.metadataLoader = metadataLoader;
        this.indexFolder = indexFolder;
    }

    public List<Map<String, Object>> query(String word, String metadataPath, String bookFolder) throws QueryEngineException {
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, List<Integer>> wordOccurrences = loadWordIndices(word);

        if (wordOccurrences == null || wordOccurrences.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Book> metadataMap;
        try {
            metadataMap = metadataLoader.loadMetadata(metadataPath);
        } catch (IOException e) {
            throw new QueryEngineException("Error loading metadata", e);
        }

        saveFoundData(bookFolder, wordOccurrences.keySet(), metadataMap, word, results);
        return results;
    }

    private Map<String, List<Integer>> loadWordIndices(String word) throws QueryEngineException {
        String path = constructFilePath(word, indexFolder);
        File file = new File(path);

        if (!file.exists()) {
            return null;
        }

        Map<String, List<Integer>> wordIndex = new HashMap<>();
        findIndexedWord(path, wordIndex);
        return wordIndex;
    }

    private static void findIndexedWord(String path, Map<String, List<Integer>> wordIndex) throws QueryEngineException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String bookId = parts[0];
                List<Integer> positions = new ArrayList<>();
                for (String pos : parts[1].split(";")) {
                    try {
                        positions.add(Integer.parseInt(pos));
                    } catch (NumberFormatException e) {
                        // Handle malformed position data, skip this part
                        continue;
                    }
                }
                wordIndex.put(bookId, positions);
            }
        } catch (IOException e) {
            throw new QueryEngineException("Error reading index file", e);
        }
    }

    private String constructFilePath(String word, String indexFolder) {
        int depth = Math.min(word.length(), 3);
        StringBuilder pathBuilder = new StringBuilder(indexFolder);
        for (int i = 0; i < depth; i++) {
            pathBuilder.append("/").append(word.charAt(i));
        }
        pathBuilder.append("/").append(word).append(".csv");
        return pathBuilder.toString();
    }

    private static void saveFoundData(String bookFolder, Set<String> booksWithWord, Map<String, Book> metadataMap, String word, List<Map<String, Object>> results) throws QueryEngineException {
        for (String bookId : booksWithWord) {
            Book metadata = metadataMap.get(bookId);
            if (metadata == null) {
                System.out.println("Metadata for book ID '" + bookId + "' not found.");
                continue;
            }

            String bookPath = String.format("%s/%s_%s.txt", bookFolder, metadata.getName(), bookId);
            System.out.println(bookPath);
            Map<String, Object> extractedData = new ParagraphExtractor().findParagraphs(bookPath, word);
            List<String> paragraphs = (List<String>) extractedData.get("paragraphs");
            int occurrences = (int) extractedData.get("occurrences");
            saveResult(results, paragraphs, metadata, occurrences);
        }
    }

    private static void saveResult(List<Map<String, Object>> results, List<String> paragraphs, Book metadata, int occurrences) {
        if (!paragraphs.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("book_id", metadata.getId());
            result.put("book_name", metadata.getName());
            result.put("author_name", metadata.getAuthor());
            result.put("URL", metadata.getUrl());
            result.put("paragraphs", paragraphs);
            result.put("total_occurrences", occurrences);
            results.add(result);
        }
    }

    private static class ParagraphExtractor {
        public Map<String, Object> findParagraphs(String bookPath, String word) throws QueryEngineException {
            Map<String, Object> result = new HashMap<>();
            List<String> paragraphs = new ArrayList<>();
            int totalOccurrences = 0;
            StringBuilder paragraphBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(bookPath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    paragraphBuilder.append(line).append("\n");
                    if (line.trim().isEmpty()) {
                        totalOccurrences = processParagraph(word, paragraphBuilder, paragraphs, totalOccurrences);
                        paragraphBuilder.setLength(0);
                    }
                }
                if (paragraphBuilder.length() > 0) {
                    totalOccurrences = processParagraph(word, paragraphBuilder, paragraphs, totalOccurrences);
                }
            } catch (IOException e) {
                throw new QueryEngineException("Error reading book file", e);
            }
            result.put("paragraphs", paragraphs);
            result.put("occurrences", totalOccurrences);
            return result;
        }

        private int processParagraph(String word, StringBuilder paragraphBuilder, List<String> paragraphs, int totalOccurrences) {
            String paragraph = paragraphBuilder.toString().trim();
            if (!paragraph.isEmpty()) {
                int occurrences = countOccurrences(paragraph, word);
                if (occurrences > 0) {
                    String highlightedParagraph = highlightWord(paragraph, word);
                    paragraphs.add(highlightedParagraph);
                    totalOccurrences += occurrences;
                }
            }
            return totalOccurrences;
        }

        private int countOccurrences(String paragraph, String word) {
            int occurrences = 0;
            String lowerParagraph = paragraph.toLowerCase();
            String regex = "(?i)\\b" + word + "\\b";
            occurrences += (lowerParagraph.length() - lowerParagraph.replaceAll(regex, "").length()) / word.length();
            return occurrences;
        }

        private String highlightWord(String paragraph, String word) {
            String blueStart = "\033[34m";
            String reset = "\033[0m";
            String regex = "(?i)\\b" + word + "\\b";
            return paragraph.replaceAll(regex, blueStart + "$0" + reset);
        }
    }
}
