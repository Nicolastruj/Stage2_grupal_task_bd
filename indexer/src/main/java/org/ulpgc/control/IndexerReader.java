package org.ulpgc.control;

import org.ulpgc.model.Book;

import java.util.List;

public interface IndexerReader {
    public List<Book> read(String bookPath);
}
