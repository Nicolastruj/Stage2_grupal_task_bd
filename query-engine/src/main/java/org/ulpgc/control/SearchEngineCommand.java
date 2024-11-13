package org.ulpgc.control;

import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.InvertedIndexLoaderAggregated;
import org.ulpgc.implementations.MetadataCSVLoader;
import org.ulpgc.implementations.QueryEngineAggregated;
import java.nio.file.*;
import java.util.*;
import org.ulpgc.ports.Input;
import org.ulpgc.ports.Output;


public class SearchEngineCommand implements Command{
    private final Input inputInterface;
    private final Output outputInterface;
    public SearchEngineCommand(Input inputInterface, Output outputInterface) {
        this.inputInterface = inputInterface;
        this.outputInterface = outputInterface;
    }
    @Override
    public void Controller() throws QueryEngineException {
        System.out.println("\nWelcome to the Search Engine!");
        System.out.println("If you want to exit the search engine, type 'EXIT'");

        Path bookFolder = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        Path indexFolder = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "metadata.csv");

        InvertedIndexLoaderAggregated indexLoader = new InvertedIndexLoaderAggregated();
        MetadataCSVLoader metadataLoader = new MetadataCSVLoader();
        QueryEngineAggregated queryEngine = new QueryEngineAggregated(indexLoader, metadataLoader);

        searcher(queryEngine, indexFolder, metadataPath, bookFolder);
    }

    private void searcher(QueryEngineAggregated queryEngine, Path indexFolder, Path metadataPath, Path bookFolder) throws QueryEngineException {
        while (true) {
            String input = inputInterface.input();
            if ("EXIT".equalsIgnoreCase(input)) {
                System.out.println("Exiting the search engine. Have a great day!");
                break;
            }
            try {
                List<Map<String, Object>> results = queryEngine.query(input, indexFolder.toString(), metadataPath.toString(), bookFolder.toString());
                outputInterface.output(results, input);
            } catch (Exception e) {
                throw new QueryEngineException(e.getMessage(), e);
            }
        }
    }
}
