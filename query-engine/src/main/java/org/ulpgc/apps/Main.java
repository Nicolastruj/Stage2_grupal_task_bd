package org.ulpgc.apps;

import org.ulpgc.control.SearchEngineCommand;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.*;
import org.ulpgc.ports.Input;
import org.ulpgc.ports.Output;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws QueryEngineException {

        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        Input input = new SearchInput();
        Output output = new SearchOutput();
        InvertedIndexLoaderAggregated invertedIndex = new InvertedIndexLoaderAggregated();
        MetadataCSVLoader metadataLoader = new MetadataCSVLoader();
        QueryEngineAggregated queryEngine = new QueryEngineAggregated(metadataLoader, invertedIndexPath.toString());
        SearchEngineCommand controller = new SearchEngineCommand(input, output, invertedIndex, metadataLoader, queryEngine);
        try {
            controller.execute();
        } catch (QueryEngineException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }
    }
}
