package org.ulpgc.ports;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;

import java.util.List;

public interface IndexerReader {
    public List<Book> read(String path) throws IndexerException;
    public String getPath() throws IndexerException;
}
