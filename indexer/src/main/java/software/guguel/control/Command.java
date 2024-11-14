package software.guguel.control;

import software.guguel.exceptions.IndexerException;

public interface Command {
    void execute() throws IndexerException;
}
