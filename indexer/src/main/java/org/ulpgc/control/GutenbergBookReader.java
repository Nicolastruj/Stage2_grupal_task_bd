package org.ulpgc.control;

import org.ulpgc.model.Book;

import java.util.List;

public class GutenbergBookReader implements IndexerReader {
    @Override
    public List<Book> read(String bookPath) {
        return List.of();
    }
}
