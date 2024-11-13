package org.ulpgc.implementations;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;
import org.ulpgc.ports.IndexerStore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class HierarchicalCsvStore implements IndexerStore {

    private static Path invertedIndexPath;
    private static int maxDepth = 3;

    public HierarchicalCsvStore(Path invertedIndexPath) {
        HierarchicalCsvStore.invertedIndexPath = invertedIndexPath;
    }

    @Override
    public void index(Book book) throws IndexerException {
        String content = book.getContent();

        String[] words = content.split("\\W+");

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                System.out.println("Indexing word: " + word + " in book " + book.getBookId() + " at position " + i);
                addFileToDirectoryStructureAppending2(book.getBookId(), i, word.toLowerCase());
            }
        }
    }

    public static void addFileToDirectoryStructureAppending(String bookId, int position, String word) throws IndexerException {
        Path currentPath = invertedIndexPath;

        try {
            int depth = Math.min(maxDepth, word.length());
            for (int i = 0; i < depth; i++) {
                String letter = String.valueOf(word.charAt(i));
                currentPath = currentPath.resolve(letter);

                if (!Files.exists(currentPath)) {
                    Files.createDirectories(currentPath);
                    System.out.println("Created directory: " + currentPath);
                }
            }

            Path filePath = currentPath.resolve(word + ".csv");
            if (!Files.exists(filePath)) {
                String fileContent = "Book ID,Position\n";
                Files.write(filePath, fileContent.getBytes());
                System.out.println("Created CSV file: " + filePath);
            }

            String csvEntry = bookId + "," + position + "\n";
            Files.write(filePath, csvEntry.getBytes(), java.nio.file.StandardOpenOption.APPEND);
            System.out.println("Appended to CSV file: " + filePath);

        } catch (IOException e) {
            throw new IndexerException(e.getMessage(), e);
        }
    }

    public static void addFileToDirectoryStructureAppending2(String bookId, int position, String word) throws IndexerException {
        Path currentPath = invertedIndexPath;

        try {
            int depth = Math.min(maxDepth, word.length());
            for (int i = 0; i < depth; i++) {
                String letter = String.valueOf(word.charAt(i));
                currentPath = currentPath.resolve(letter);

                if (!Files.exists(currentPath)) {
                    Files.createDirectories(currentPath);
                    System.out.println("Created directory: " + currentPath);
                }
            }

            Path filePath = currentPath.resolve(word + ".csv");
            Map<String, List<Integer>> indexMap = new HashMap<>();

            // Leer el archivo existente si ya contiene datos
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                for (String line : lines) {
                    String[] parts = line.split(",", 2);
                    String existingBookId = parts[0];
                    List<Integer> positions = Arrays.stream(parts[1].split(";"))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    indexMap.put(existingBookId, positions);
                }
            }

            // Agregar la nueva posición al `bookId` correspondiente
            indexMap.putIfAbsent(bookId, new ArrayList<>());
            indexMap.get(bookId).add(position);

            // Escribir de nuevo el archivo con el nuevo formato
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Map.Entry<String, List<Integer>> entry : indexMap.entrySet()) {
                    String line = entry.getKey() + "," + entry.getValue().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(";"));
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Updated CSV file: " + filePath);

        } catch (IOException e) {
            throw new IndexerException(e.getMessage(), e);
        }
    }

    public static void setMaxDepth(int maxDepth) {
        HierarchicalCsvStore.maxDepth = maxDepth;
    }
}
