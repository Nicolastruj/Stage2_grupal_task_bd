package org.ulpgc.apps;

import org.ulpgc.control.Command;
import org.ulpgc.control.SearchEngineCommand;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.QueryEngineExpanded;
import org.ulpgc.implementations.SearchInput;
import org.ulpgc.implementations.SearchOutput;
import org.ulpgc.ports.Input;
import org.ulpgc.ports.Output;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MainWithExpandedStructure {
    public static void main(String[] args) throws QueryEngineException {
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "InvertedIndex");
        Path bookFolderPath = Paths.get(System.getProperty("user.dir"), "BookDatalake");
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "metadata.csv");

        Input input = new SearchInput();
        Output output = new SearchOutput();
        
        // Pass the Path object directly instead of converting it to a String
        QueryEngineExpanded queryEngine = new QueryEngineExpanded(
                metadataPath.toString(),
                bookFolderPath.toString(),
                invertedIndexPath
        );
        
        Command searchEngineCommand = new SearchEngineCommand(input, output, queryEngine);

        try {
            searchEngineCommand.execute();
        } catch (QueryEngineException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }
    }
}
