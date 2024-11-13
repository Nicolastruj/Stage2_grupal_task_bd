package org.ulpgc.control;

import org.ulpgc.exceptions.QueryException;

public interface Command {
    void execute() throws QueryException;
}
