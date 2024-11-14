package org.ulpgc;

import org.openjdk.jmh.annotations.*;
import org.ulpgc.exceptions.QueryEngineException;
import org.ulpgc.implementations.QueryEngineAggregated;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 5, time = 1)

public class QueryEngineTest {
    @State(Scope.Thread)
    public static class QueryEnginePath {
        public static Path bookDatalakePath;
        public static Path invertedIndexPath;
        public static Path metaDataPath;

        @Param({"man", "immediate imminent"})
        public static String[] word;
        @Setup(Level.Trial)
        public void setup() {
            invertedIndexPath = Paths.get(System.getProperty("user.dir"), "..", "InvertedIndex");
            bookDatalakePath = Paths.get(System.getProperty("user.dir"), "..", "BookDatalake");
            metaDataPath = Paths.get(System.getProperty("user.dir"), "..", "metadata.csv");
        }
    }

    @Benchmark
    public void aggregateQueryEngine() {
        QueryEngineAggregated queryEngine = new QueryEngineAggregated(
                QueryEnginePath.metaDataPath.toString(),
                QueryEnginePath.bookDatalakePath.toString(),
                QueryEnginePath.invertedIndexPath.toString()
        );
        try {
            queryEngine.query(QueryEnginePath.word);
        } catch (QueryEngineException e) {
            throw new RuntimeException(e);
        }
    }
}
