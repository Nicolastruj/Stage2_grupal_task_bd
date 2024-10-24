package org.ulpgc.control;

public class IndexerController {
    private final IndexerReader indexerReader;
    private final IndexerStore indexerStore;

    public IndexerController(IndexerReader indexerReader, IndexerStore indexerStore) {
        this.indexerReader = indexerReader;
        this.indexerStore = indexerStore;
    }

    public void execute(){
        //TODO indexerReader.read(bookPath)
        //TODO indexerStore.index(book)
    }
}
