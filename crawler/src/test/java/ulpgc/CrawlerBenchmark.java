package ulpgc;

import org.openjdk.jmh.annotations.*;
import org.ulpgc.control.Command;
import org.ulpgc.control.CrawlerCommand;
import org.ulpgc.implementations.ReaderFromWeb;
import org.ulpgc.implementations.StoreInDatalake;
import org.ulpgc.ports.ReaderFromWebInterface;
import org.ulpgc.ports.StoreInDatalakeInterface;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 5, time = 1)
public class CrawlerBenchmark {
    @Benchmark
    public void crawler() {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "..", "BookDatalake").normalize();
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "..", "metadata.csv").normalize();
        ReaderFromWebInterface reader = new ReaderFromWeb();
        StoreInDatalakeInterface store = new StoreInDatalake(metadataPath.toString());
        Command crawlerCommand = new CrawlerCommand(bookDatalakePath.toString(), metadataPath.toString(), reader, store);
        crawlerCommand.download();
    }
}
