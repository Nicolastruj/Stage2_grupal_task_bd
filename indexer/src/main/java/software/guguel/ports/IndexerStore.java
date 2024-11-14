package software.guguel.ports;

import software.guguel.exceptions.IndexerException;
import software.guguel.model.Book;

public interface IndexerStore {
    void index(Book book) throws IndexerException;
}
