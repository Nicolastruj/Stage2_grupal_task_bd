package org.ulpgc.implementations;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.ports.IndexLoader;

import java.io.*;
import java.util.*;
public class InvertedIndexLoaderAggregated implements IndexLoader {
    @Override
    public Map<String, List<Integer>> loadWord(String word, String indexFolder) throws QueryEngineException {
        String path = constructFilePath(word, indexFolder);
        File file = new File(path);
        if (!file.exists()) {
            return new HashMap<>();
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
                String bookId = parts[0];
                List<Integer> positions = new ArrayList<>();
                if (parts.length > 1) {
                    for (String pos : parts[1].split(";")) {
                        positions.add(Integer.parseInt(pos));
                    }
                }
                wordIndex.put(bookId, positions);
            }
        } catch (IOException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }
    }

    private String constructFilePath(String word, String indexFolder) {
            int depth = Math.min(word.length(), 3);
            StringBuilder pathBuilder = new StringBuilder(indexFolder);
            for (int i = 0; i < depth; i++) {
                pathBuilder.append("/").append(word.charAt(i));
            }
            // Always add the full word at the end
            pathBuilder.append("/").append(word).append(".csv");
            return pathBuilder.toString();
        }
}