package software.guguel.implementations;

import software.guguel.exceptions.IndexerException;
import software.guguel.model.Book;
import software.guguel.ports.IndexerStore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class AggregatedHierarchicalCsvStore implements IndexerStore {

    private static Path invertedIndexPath;
    private static final int maxDepth = 3;

    public AggregatedHierarchicalCsvStore(Path invertedIndexPath) {
        AggregatedHierarchicalCsvStore.invertedIndexPath = invertedIndexPath;
    }

    @Override
    public void index(Book book) throws IndexerException {
        String content = book.content();

        String[] words = content.split("\\W+");

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                indexWord(book.bookId(), i, word.toLowerCase());
            }
        }
    }


    public static void indexWord(String bookId, int position, String word) throws IndexerException {
        Path currentPath = invertedIndexPath;

        try {
            currentPath = getHierarchicalDirectoryPath(word, currentPath);
            Path filePath = getWordFilePath(word, currentPath);
            Map<String, List<Integer>> indexMap = getWordInfoAlreadyIndexed(filePath);
            addNewWordPositionToMap(bookId, position, indexMap);
            writeUpdatedWordInfoToFile(filePath, indexMap);

        } catch (IOException e) {
            throw new IndexerException(e.getMessage(), e);
        }
    }

    private static void writeUpdatedWordInfoToFile(Path filePath, Map<String, List<Integer>> indexMap) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Map.Entry<String, List<Integer>> entry : indexMap.entrySet()) {
                String line = entry.getKey() + "," + entry.getValue().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(";"));
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static void addNewWordPositionToMap(String bookId, int position, Map<String, List<Integer>> indexMap) {
        indexMap.putIfAbsent(bookId, new ArrayList<>());
        indexMap.get(bookId).add(position);
    }

    private static Map<String, List<Integer>> getWordInfoAlreadyIndexed(Path filePath) throws IOException {
        Map<String, List<Integer>> indexMap = new HashMap<>();

        if (Files.exists(filePath)) {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                addWordInfoToMap(line, indexMap);
            }
        }
        return indexMap;
    }

    private static void addWordInfoToMap(String line, Map<String, List<Integer>> indexMap) {
        String[] parts = line.split(",", 2);
        String existingBookId = parts[0];
        List<Integer> positions = getWordPositionsInBook(parts);
        indexMap.put(existingBookId, positions);
    }

    private static List<Integer> getWordPositionsInBook(String[] parts) {
        return Arrays.stream(parts[1].split(";"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static Path getWordFilePath(String word, Path currentPath) {
        return currentPath.resolve(word + ".csv");
    }

    private static Path getHierarchicalDirectoryPath(String word, Path currentPath) throws IOException {
        int depth = Math.min(maxDepth, word.length());
        for (int i = 0; i < depth; i++) {
            String letter = String.valueOf(word.charAt(i));
            currentPath = currentPath.resolve(letter);

            createDirectoryIfNotExists(currentPath);
        }
        return currentPath;
    }

    private static void createDirectoryIfNotExists(Path currentPath) throws IOException {
        if (!Files.exists(currentPath)) {
            Files.createDirectories(currentPath);
        }
    }

}
