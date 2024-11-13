package org.ulpgc.ports;

import org.ulpgc.exceptions.QueryException;
import org.ulpgc.model.Book;

public interface QueryStore {
    public void index(Book book) throws QueryException;
}
