package org.ulpgc.control;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
                addFileToDirectoryStructure(book.getBookId(), i, word.toLowerCase());
            }
        }
    }

    public static void addFileToDirectoryStructure(String bookId, int position, String word) throws IndexerException {
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

    public static void setMaxDepth(int maxDepth) {
        HierarchicalCsvStore.maxDepth = maxDepth;
    }
}
