package software.guguel.ports;

import software.guguel.exceptions.IndexerException;
import software.guguel.model.Book;

import java.util.List;

public interface IndexerReader {
    List<Book> read(String path) throws IndexerException;

    String getPath() throws IndexerException;
}
