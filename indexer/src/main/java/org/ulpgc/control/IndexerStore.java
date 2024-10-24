package org.ulpgc.control;

import org.ulpgc.model.Book;

public interface IndexerStore {
    public void index(Book book);
}
