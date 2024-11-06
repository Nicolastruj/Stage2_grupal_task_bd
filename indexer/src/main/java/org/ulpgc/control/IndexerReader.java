package org.ulpgc.control;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;

import java.util.List;

public interface IndexerReader {
    public List<Book> read() throws IndexerException;
}
