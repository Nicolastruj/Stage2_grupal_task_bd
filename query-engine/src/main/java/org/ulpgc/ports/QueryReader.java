package org.ulpgc.ports;

import org.ulpgc.exceptions.QueryException;
import org.ulpgc.model.Book;

import java.util.List;

public interface QueryReader {
    public List<Book> read(String path) throws QueryException;
    public String getPath() throws QueryException;
}
