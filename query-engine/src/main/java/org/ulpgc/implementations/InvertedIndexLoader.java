package org.ulpgc.implementations;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.ports.IndexLoader;

import java.io.*;
import java.util.*;
public class InvertedIndexLoader implements IndexLoader {
        @Override
        public Map<String, List<Integer>> loadWord(String word, String indexFolder) throws QueryEngineException {
            // Create a path
            String path = constructFilePath(word, indexFolder);

            // Check if the file of the word exists first
            File file = new File(path);
            if (!file.exists()) {
                return new HashMap<>();
            }

            Map<String, List<Integer>> wordIndex = new HashMap<>();

            // Open the file if it exists
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

            return wordIndex;
        }

        private String constructFilePath(String word, String indexFolder) {

            int depth = Math.min(word.length(), 3);
            StringBuilder pathBuilder = new StringBuilder(indexFolder);

            for (int i = 0; i < depth; i++) {
                pathBuilder.append("/").append(word.charAt(i));  // Add the first 1, 2 or 3 characters as subdirectories
            }

            // Always add the full word at the end
            pathBuilder.append("/").append(word).append(".csv");

            return pathBuilder.toString();
        }
}