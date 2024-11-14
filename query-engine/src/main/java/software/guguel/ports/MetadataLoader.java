package software.guguel.ports;

import software.guguel.exceptions.QueryEngineException;
import software.guguel.model.Book;
import java.io.IOException;
import java.util.Map;

public interface MetadataLoader {
    Map<String, Book> loadMetadata(String metadataPath) throws IOException, QueryEngineException;
}
