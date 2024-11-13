package org.ulpgc.control;

import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.InvertedIndexLoader;
import org.ulpgc.implementations.MetadataCSVLoader;
import org.ulpgc.implementations.QueryEngine1;

import java.nio.file.*;
import java.util.*;

public class QueryEngineController {

    public static void Controller() throws QueryEngineException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWelcome to the Search Engine!");
        System.out.println("If you want to exit the search engine, type 'EXIT'");

        // Paths according to the described structure
        Path bookFolder = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        Path indexFolder = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "metadata.csv");

        // Create instances of the loaders and query engine
        InvertedIndexLoader indexLoader = new InvertedIndexLoader();
        MetadataCSVLoader metadataLoader = new MetadataCSVLoader();
        QueryEngine1 queryEngine = new QueryEngine1(indexLoader, metadataLoader);

        while (true) {
            System.out.print("\nEnter the word/words you want to search for: ");
            String userInput = scanner.nextLine().trim();

            if ("EXIT".equalsIgnoreCase(userInput)) {
                System.out.println("Exiting the search engine. Have a great day!");
                break;
            }

            try {
                List<Map<String, Object>> results = queryEngine.query(userInput, indexFolder.toString(), metadataPath.toString(), bookFolder.toString());

                if (!results.isEmpty()) {
                    System.out.println("\nResults for '" + userInput + "':");
                    for (Map<String, Object> result : results) {
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
                } else {
                    System.out.println("\nSorry! No results found for that word.");
                }
            } catch (Exception e) {
                throw new QueryEngineException(e.getMessage(), e);
            }
        }

        scanner.close();
    }
}
