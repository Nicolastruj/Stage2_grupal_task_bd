package org.ulpgc.control;

import org.ulpgc.exceptions.IndexerException;

public interface Command {
    void execute() throws IndexerException;
}
