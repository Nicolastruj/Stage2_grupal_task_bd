package org.ulpgc.implementations;

import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.model.Book;
import org.ulpgc.model.QueryEngine;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryEngineAggregated implements QueryEngine {
    private final String metadataPath;
    private final String bookFolder;
    private final String indexFolder;

    public QueryEngineAggregated(String metadataPath, String bookFolder, String indexFolder) {
        this.metadataPath = metadataPath;
        this.bookFolder = bookFolder;
        this.indexFolder = indexFolder;
    }

    @Override
    public List<Map<String, Object>> query(String[] words) throws QueryEngineException {
        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> commonBooks = null;

        for (String word : words) {
            word = word.trim();
            Map<String, List<Integer>> wordOccurrences = loadIndexedWordInfo(word);
            if (wordOccurrences == null || wordOccurrences.isEmpty()) {
                return Collections.emptyList();
            }

            commonBooks = getCommonBooks(commonBooks, wordOccurrences);
        }

        if (commonBooks == null || commonBooks.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Book> metadataMap;
        metadataMap = loadMetadata(metadataPath);
        for (String bookId : commonBooks) {
            Book metadata = metadataMap.get(bookId);
            if (metadata == null) {
                System.out.println("Metadata for book ID '" + bookId + "' not found.");
                continue;
            }

            String bookPath = String.format("%s/%s_%s.txt", bookFolder, metadata.getName(), bookId);
            Map<String, Object> extractedData = new ParagraphExtractor().findParagraphs(bookPath, words);
            List<String> paragraphs = (List<String>) extractedData.get("paragraphs");
            int occurrences = (int) extractedData.get("occurrences");
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

        return results;
    }

    private static Set<String> getCommonBooks(Set<String> commonBooks, Map<String, List<Integer>> wordOccurrences) {
        if (commonBooks == null) {
            commonBooks = new HashSet<>(wordOccurrences.keySet());
        } else {
            commonBooks.retainAll(wordOccurrences.keySet());
        }
        return commonBooks;
    }

    private Map<String, List<Integer>> loadIndexedWordInfo(String word) throws QueryEngineException {
        String wordFilePath = constructWordFilePath(word, indexFolder);
        File file = new File(wordFilePath);

        if (!file.exists()) {
            return null;
        }

        Map<String, List<Integer>> wordIndex = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(wordFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                String bookId = parts[0];
                List<Integer> positions = getWordPositions(parts);
                wordIndex.put(bookId, positions);
            }
        } catch (IOException e) {
            throw new QueryEngineException("Error reading index file", e);
        }
        return wordIndex;
    }

    private static List<Integer> getWordPositions(String[] parts) {
        List<Integer> positions = new ArrayList<>();
        for (String pos : parts[1].split(";")) {
            positions.add(Integer.parseInt(pos));
        }
        return positions;
    }


    private String constructWordFilePath(String word, String indexFolder) {
        int depth = Math.min(word.length(), 3);
        StringBuilder pathBuilder = new StringBuilder(indexFolder);
        for (int i = 0; i < depth; i++) {
            pathBuilder.append("/").append(word.charAt(i));
        }
        pathBuilder.append("/").append(word).append(".csv");
        return pathBuilder.toString();
    }

    public static class ParagraphExtractor {
        public Map<String, Object> findParagraphs(String bookPath, String[] searchWords) throws QueryEngineException {
            Map<String, Object> result = new HashMap<>();
            List<String> relevantParagraphs = new ArrayList<>();
            int totalOccurrences = 0;

            Map<String, Pattern> wordPatterns = new HashMap<>();
            for (String word : searchWords) {
                wordPatterns.put(word, Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE));
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(bookPath))) {
                StringBuilder textBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    textBuilder.append(line).append("\n");
                }

                String[] paragraphs = textBuilder.toString().split("\\n\\n");

                for (String paragraph : paragraphs) {
                    boolean paragraphAdded = false;

                    for (Map.Entry<String, Pattern> entry : wordPatterns.entrySet()) {
                        Matcher matcher = entry.getValue().matcher(paragraph);

                        if (matcher.find()) {
                            int occurrencesInParagraph = 0;
                            StringBuilder highlightedBuffer = new StringBuilder();
                            do {
                                occurrencesInParagraph++;
                                matcher.appendReplacement(highlightedBuffer, "\033[34m" + matcher.group() + "\033[0m");
                            } while (matcher.find());

                            matcher.appendTail(highlightedBuffer);
                            paragraph = highlightedBuffer.toString();
                            totalOccurrences += occurrencesInParagraph;

                            if (!paragraphAdded) {
                                relevantParagraphs.add(paragraph.trim());
                                paragraphAdded = true;
                            }
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                throw new QueryEngineException("Error: Book file not found: " + bookPath, e);
            } catch (IOException e) {
                throw new QueryEngineException("Error reading book file", e);
            }

            result.put("paragraphs", relevantParagraphs);
            result.put("occurrences", totalOccurrences);
            return result;
        }
    }


    public Map<String, Book> loadMetadata(String metadataPath) throws QueryEngineException {
        try (BufferedReader reader = new BufferedReader(new FileReader(metadataPath))) {
            return readMetadata(reader);
        } catch (FileNotFoundException e) {
            throw new QueryEngineException(e.getMessage(), e);
        } catch (IOException e) {
            throw new QueryEngineException("Error reading metadata file", e);
        }
    }

    private Map<String, Book> readMetadata(BufferedReader reader) throws IOException {
        Map<String, Book> metadata = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                metadata.put(parts[1], new Book(parts[1], parts[2], parts[3], parts[4]));
            }
        }
        return metadata;
    }
}
