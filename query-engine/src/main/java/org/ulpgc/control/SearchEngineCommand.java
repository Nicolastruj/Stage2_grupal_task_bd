package org.ulpgc.control;

import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.InvertedIndexLoaderAggregated;
import org.ulpgc.implementations.MetadataCSVLoader;
import org.ulpgc.implementations.QueryEngineAggregated;
import org.ulpgc.ports.Input;
import org.ulpgc.ports.Output;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class SearchEngineCommand implements Command {
    private final Input input;
    private final Output outputInterface;
    private final QueryEngineAggregated queryEngine;

    public SearchEngineCommand(Input inputInterface, Output outputInterface, InvertedIndexLoaderAggregated invertedIndex, MetadataCSVLoader metadataLoader, QueryEngineAggregated queryEngine) {
        this.input = inputInterface;
        this.outputInterface = outputInterface;
        this.queryEngine = queryEngine;
    }

    @Override
    public void execute() throws QueryEngineException {
        System.out.println("\nWelcome to the Search Engine!");
        System.out.println("If you want to exit the search engine, type 'EXIT'");

        Path bookFolder = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        Path indexFolder = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "metadata.csv");

        searcher(queryEngine, indexFolder, metadataPath, bookFolder);
    }

    private void searcher(QueryEngineAggregated queryEngine, Path indexFolder, Path metadataPath, Path bookFolder) throws QueryEngineException {
        while (true) {
            String searchInput = input.getSearchText();

            String[] words = searchInput.split("\\s+");

            if ("EXIT".equalsIgnoreCase(words[0])) {
                System.out.println("Exiting the search engine. Have a great day!");
                return;
            }

            if (words.length == 0 || words[0].trim().isEmpty()) {
                System.out.println("Empty query entered. Please try again.");
                continue;
            }

            Set<String> commonBooks = new HashSet<>();
            List<Map<String, Object>> results = new ArrayList<>();

            try {
                for (int i = 0; i < words.length; i++) {
                    String inputWord = words[i].trim();

                    List<Map<String, Object>> wordResults = queryEngine.query(inputWord, metadataPath.toString(), bookFolder.toString());

                    if (i == 0) {
                        commonBooks = extractBookIdsFromResults(wordResults);
                    } else {
                        Set<String> currentBooks = extractBookIdsFromResults(wordResults);
                        commonBooks.retainAll(currentBooks);
                    }

                    results.addAll(wordResults);
                }

                List<Map<String, Object>> finalResults = new ArrayList<>();
                for (Map<String, Object> result : results) {
                    String bookId = (String) result.get("book_id");
                    if (bookId != null && commonBooks.contains(bookId)) {
                        finalResults.add(result);
                    }
                }

                outputInterface.output(finalResults, searchInput);

            } catch (Exception e) {
                System.err.println("An error occurred while processing the query: " + e.getMessage());
                throw new QueryEngineException(e.getMessage(), e);
            }
        }
    }

    private Set<String> extractBookIdsFromResults(List<Map<String, Object>> results) {
        Set<String> bookIds = new HashSet<>();
        for (Map<String, Object> result : results) {
            String bookId = (String) result.get("book_id");
            if (bookId != null) {
                bookIds.add(bookId);
            }
        }
        return bookIds;
    }


}
