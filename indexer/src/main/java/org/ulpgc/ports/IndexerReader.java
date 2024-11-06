package org.ulpgc.ports;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;

import java.util.List;

public interface IndexerReader {
    List<Book> read() throws IndexerException;
}
