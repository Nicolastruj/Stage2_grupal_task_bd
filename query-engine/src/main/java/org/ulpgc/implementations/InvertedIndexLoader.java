package org.ulpgc.implementations;
import org.ulpgc.ports.IndexLoader;

import java.io.*;
import java.util.*;
public class InvertedIndexLoader implements IndexLoader{
    @Override
    public Map<String, List<Integer>> loadWord(String word, String indexFolder) throws IOException {
        if (word.length() < 3) return null;

        String path = String.format("%s/%c/%c/%c/%s.csv",
                indexFolder, word.charAt(0), word.charAt(1), word.charAt(2), word);

        Map<String, List<Integer>> wordIndex = new HashMap<>();

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
        }

        return wordIndex;
    }
}
