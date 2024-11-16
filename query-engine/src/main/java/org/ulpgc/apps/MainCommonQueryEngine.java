package org.ulpgc.apps;

import org.ulpgc.control.Command;
import org.ulpgc.control.SearchEngineCommand;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.CommonQueryEngine;
import org.ulpgc.implementations.SearchInput;
import org.ulpgc.implementations.SearchOutput;
import org.ulpgc.ports.Input;
import org.ulpgc.ports.Output;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MainCommonQueryEngine {
    public static void main(String[] args) throws QueryEngineException {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "/data/datalake");
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "/data/datamart");
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "/data/metadata/metadata.csv");

        Input input = new SearchInput();
        Output output = new SearchOutput();
        CommonQueryEngine queryEngine = new CommonQueryEngine(
                metadataPath.toString(),
                bookDatalakePath.toString(),
                invertedIndexPath.toString()
        );
        Command searchEngineCommand = new SearchEngineCommand(input, output, queryEngine);

        try {
            searchEngineCommand.execute();
        } catch (QueryEngineException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }
    }
}
