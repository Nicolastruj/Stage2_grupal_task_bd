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

    @State(Scope.Benchmark) // Cambia el alcance a Benchmark para asegurar la inicialización previa
    public static class QueryEnginePath {
        public Path bookDatalakePath;
        public Path invertedIndexPath;
        public Path metaDataPath;

        @Param({"man", "immediate imminent"})
        public String word;

        @Setup(Level.Trial)
        public void setup() {
            invertedIndexPath = Paths.get(System.getProperty("user.dir"), "..", "InvertedIndex");
            bookDatalakePath = Paths.get(System.getProperty("user.dir"), "..", "BookDatalake");
            metaDataPath = Paths.get(System.getProperty("user.dir"), "..", "metadata.csv");
        }
    }

    @Benchmark
    public void aggregateQueryEngine(QueryEnginePath path) {
        QueryEngineAggregated queryEngine = new QueryEngineAggregated(
                path.metaDataPath.toString(),
                path.bookDatalakePath.toString(),
                path.invertedIndexPath.toString()
        );
        try {
            queryEngine.query(new String[]{path.word});  // Pasa la palabra como un arreglo de un solo elemento
        } catch (QueryEngineException e) {
            throw new RuntimeException(e);
        }
    }
}
