package org.ulpgc.ports;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;

public interface IndexerStore {
    void index(Book book) throws IndexerException;
}
