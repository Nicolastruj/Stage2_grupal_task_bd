package software.guguel.implementations;

import software.guguel.exceptions.IndexerException;
import software.guguel.model.Book;
import software.guguel.ports.IndexerStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExpandedHierarchicalCsvStore implements IndexerStore {

    private static Path invertedIndexPath;
    private static final int maxDepth = 3;

    public ExpandedHierarchicalCsvStore(Path invertedIndexPath) {
        ExpandedHierarchicalCsvStore.invertedIndexPath = invertedIndexPath;
    }

    @Override
    public void index(Book book) throws IndexerException {
        String content = book.content();
        String bookId = book.bookId();
        String[] words = content.split("\\W+");

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                indexWord(bookId, i, word.toLowerCase());
            }
        }
    }


    public static void indexWord(String bookId, int position, String word) throws IndexerException {
        Path currentPath = invertedIndexPath;

        try {
            currentPath = getHierarchicalDirectoryPath(word, currentPath);
            Path filePath = getWordFilePath(word, currentPath);
            initializeWordFileIfNotExists(filePath);
            writeWordInfoToFile(bookId, position, filePath);

        } catch (IOException e) {
            throw new IndexerException(e.getMessage(), e);
        }
    }

    private static Path getHierarchicalDirectoryPath(String word, Path currentPath) throws IndexerException {
        int depth = Math.min(maxDepth, word.length());
        for (int i = 0; i < depth; i++) {
            String letter = String.valueOf(word.charAt(i));
            currentPath = currentPath.resolve(letter);

            createDirectoryIfNotExists(currentPath);
        }
        return currentPath;
    }

    private static void createDirectoryIfNotExists(Path currentPath) throws IndexerException {
        if (!Files.exists(currentPath)) {
            try {
                Files.createDirectories(currentPath);
            } catch (IOException e) {
                throw new IndexerException(e.getMessage(), e);
            }
        }
    }

    private static Path getWordFilePath(String word, Path currentPath) {
        return currentPath.resolve(word + ".csv");
    }

    private static void initializeWordFileIfNotExists(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            String fileContent = "Book ID,Position\n";
            Files.write(filePath, fileContent.getBytes());
        }
    }

    private static void writeWordInfoToFile(String bookId, int position, Path filePath) throws IOException {
        String csvEntry = bookId + "," + position + "\n";
        Files.write(filePath, csvEntry.getBytes(), java.nio.file.StandardOpenOption.APPEND);
    }

}
