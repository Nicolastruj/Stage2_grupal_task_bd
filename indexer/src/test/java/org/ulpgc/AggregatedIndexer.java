package org.ulpgc;

import org.openjdk.jmh.annotations.*;
import org.ulpgc.control.IndexerCommand;
import org.ulpgc.exceptions.IndexerException;
import org.ulpgc.implementations.AggregatedHierarchicalCsvStore;
import org.ulpgc.implementations.ExpandedHierarchicalCsvStore;
import org.ulpgc.implementations.GutenbergBookReader;
import org.ulpgc.ports.IndexerReader;
import org.ulpgc.ports.IndexerStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 5, time = 1)
public class AggregatedIndexer {

    @State(Scope.Thread)
    public static class IndexerPath {
        public Path bookDatalakePath;
        public Path invertedIndexPath;

        @Setup(Level.Trial)
        public void setup() {
            bookDatalakePath = Paths.get(System.getProperty("user.dir"), "..", "BookDatalake").normalize();
            invertedIndexPath = Paths.get(System.getProperty("user.dir"), "..", "InvertedIndex").normalize();

        }
    }

    @Benchmark
    public void aggregatedIndexer(IndexerPath path) throws IndexerException {
        IndexerReader indexerReader = new GutenbergBookReader(path.bookDatalakePath.toString());
        IndexerStore hierarchicalCsvStore = new AggregatedHierarchicalCsvStore(path.invertedIndexPath);
        IndexerCommand hierarchicalCsvController = new IndexerCommand(indexerReader, hierarchicalCsvStore);
        hierarchicalCsvController.execute();
    }

    @Benchmark
    public void expandedIndexer(IndexerPath path) throws IndexerException {
        IndexerReader indexerReader = new GutenbergBookReader(path.bookDatalakePath.toString());
        IndexerStore hierarchicalCsvStore = new ExpandedHierarchicalCsvStore(path.invertedIndexPath);
        IndexerCommand hierarchicalCsvController = new IndexerCommand(indexerReader, hierarchicalCsvStore);
        hierarchicalCsvController.execute();
    }
}
