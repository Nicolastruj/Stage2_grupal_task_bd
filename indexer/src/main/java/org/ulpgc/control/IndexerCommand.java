package org.ulpgc.control;

import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.model.Book;
import org.ulpgc.ports.IndexerReader;
import org.ulpgc.ports.IndexerStore;

import java.util.List;

public class IndexerCommand implements Command {
    private final IndexerReader indexerReader;
    private final IndexerStore indexerStore;

    public IndexerCommand(IndexerReader indexerReader, IndexerStore indexerStore) {
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
