package org.ulpgc.control;

import org.ulpgc.exceptions.QueryEngineException;

public interface Command {
    void execute() throws QueryEngineException;
}
