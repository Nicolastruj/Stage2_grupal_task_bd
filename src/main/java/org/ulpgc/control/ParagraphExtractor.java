package org.ulpgc.control;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParagraphExtractor {

    public Map<String, Object> extractParagraphs(String bookFilename, List<String> searchWords) {
        Map<String, Object> result = new HashMap<>();
        List<String> relevantParagraphs = new ArrayList<>();
        int occurrences = 0;

        try {
            String text = new String(Files.readAllBytes(Paths.get(bookFilename)));
            String[] paragraphs = text.split("\n\n");

            for (String paragraph : paragraphs) {
                boolean found = false;
                for (String word : searchWords) {
                    Pattern pattern = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(paragraph);

                    if (matcher.find()) {
                        occurrences += (int) matcher.results().count();
                        String highlightedParagraph = paragraph.replaceAll("(?i)" + word, "\033[94m" + word + "\033[0m");
                        relevantParagraphs.add(highlightedParagraph);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            result.put("paragraphs", relevantParagraphs);
            result.put("occurrences", occurrences);
        } catch (IOException e) {
            System.out.println("Error: Book file not found: " + bookFilename);
        }

        return result;
    }
}