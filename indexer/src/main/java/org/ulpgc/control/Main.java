package org.ulpgc.control;

public class Main {
    public static void main(String[] args) {
        IndexerReader indexerReader = new GutenbergBookReader();
        IndexerStore dictionaryIndexerStore = new DictionaryIndexerStore();
        IndexerStore jsonIndexerStore = new JsonIndexerStore();
        IndexerController dictionaryIndexerController = new IndexerController(indexerReader,dictionaryIndexerStore);
        IndexerController jsonIndexerController = new IndexerController(indexerReader,jsonIndexerStore);
        //TODO dictionaryIndexerController.execute()
        //TODO jsonIndexerController.execute()
    }
}
