package org.ulpgc.control;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class IndexerController {
    private final IndexerReader indexerReader;
    private final IndexerStore indexerStore;

    public IndexerController(IndexerReader indexerReader, IndexerStore indexerStore) {
        this.indexerReader = indexerReader;
        this.indexerStore = indexerStore;
    }

    public void execute() throws IndexerException {
        //TODO indexerReader.read() only last books
        List<Book> books = indexerReader.read();

        for (Book book : books) {
            indexerStore.index(book);
        }
    }
}
