package org.ulpgc.implementations;

import org.ulpgc.ports.Output;
import java.util.List;
import java.util.Map;

public class SearchOutput implements Output {
    @Override
    public void output(List<Map<String, Object>> results, String input) {
        if (!results.isEmpty()) {
            System.out.println("\nResults for '" + input + "':");
            for (Map<String, Object> result : results) {
                printer(result);
            }
        } else {
            System.out.println("\nSorry! No results found for that word.");
        }
    }

    private static void printer(Map<String, Object> result) {
        System.out.println("Book Name: " + result.get("book_name"));
        System.out.println("Author: " + result.get("author_name"));
        System.out.println("URL: " + result.get("URL"));
        System.out.println("Total Occurrences: " + result.get("total_occurrences"));
        System.out.println("Paragraphs:");
        List<String> paragraphs = (List<String>) result.get("paragraphs");
        for (String paragraph : paragraphs) {
            System.out.println(paragraph + "\n\n");
        }
    }
}