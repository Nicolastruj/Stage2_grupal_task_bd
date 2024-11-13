package org.ulpgc.ports;

import org.ulpgc.exceptions.QueryException;
import org.ulpgc.model.Book;
import java.io.IOException;
import java.util.Map;

public interface MetadataLoader {
    Map<String, Book> loadMetadata(String metadataPath) throws IOException, QueryException;
}