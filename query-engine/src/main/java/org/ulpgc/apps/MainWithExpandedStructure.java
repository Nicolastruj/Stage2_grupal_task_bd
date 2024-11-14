package org.ulpgc.apps;

import org.ulpgc.control.SearchExpandedCommand;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.SearchInput;
import org.ulpgc.implementations.SearchOutput;
import org.ulpgc.ports.Input;
import org.ulpgc.ports.Output;

public class MainWithExpandedStructure {
    public static void main(String[] args) throws QueryEngineException{
        Input input = new SearchInput();
        Output output = new SearchOutput();
        SearchExpandedCommand controller = new SearchExpandedCommand(input, output);
        try {
            controller.Controller();
        } catch (QueryEngineException e) {
            throw new QueryEngineException(e.getMessage(), e);
        }
    }
}
