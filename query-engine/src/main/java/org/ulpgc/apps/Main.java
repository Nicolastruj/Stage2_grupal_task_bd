package org.ulpgc.apps;

import org.ulpgc.control.SearchEngineCommand;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.SearchInput;
import org.ulpgc.implementations.SearchOutput;
import org.ulpgc.ports.Input;
import org.ulpgc.ports.Output;

public class Main {
    public static void main(String[] args) throws QueryEngineException{
        Input input = new SearchInput();
        Output output = new SearchOutput();
        SearchEngineCommand controller = new SearchEngineCommand(input, output);
        try {
            controller.Controller();
        } catch (QueryEngineException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }
    }
}
