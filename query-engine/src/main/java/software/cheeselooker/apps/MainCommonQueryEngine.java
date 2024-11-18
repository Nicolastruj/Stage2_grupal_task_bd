import spark.Spark;
import software.cheeselooker.control.SearchEngineCommand;
import software.cheeselooker.exceptions.QueryEngineException;
import software.cheeselooker.implementations.CommonQueryEngine;
import software.cheeselooker.implementations.SearchInput;
import software.cheeselooker.implementations.SearchOutput;
import software.cheeselooker.ports.Input;
import software.cheeselooker.ports.Output;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class MainCommonQueryEngine {
    public static void main(String[] args) {
        Path bookDatalakePath = Paths.get(System.getProperty("user.dir"), "/data/datalake");
        Path invertedIndexPath = Paths.get(System.getProperty("user.dir"), "/data/datamart");
        Path metadataPath = Paths.get(System.getProperty("user.dir"), "/data/metadata/metadata.csv");

        Input input = new SearchInput();
        Output output = new SearchOutput();
        CommonQueryEngine queryEngine = new CommonQueryEngine(
                metadataPath.toString(),
                bookDatalakePath.toString(),
                invertedIndexPath.toString()
        );

        Spark.port(8080); // Expose the application on port 8080

        Spark.get("/search", (req, res) -> {
            String query = req.queryParams("q");
            if (query == null || query.trim().isEmpty()) {
                res.status(400);
                return "Query parameter 'q' is required.";
            }

            String[] words = query.split("\\s+");
            try {
                List<Map<String, Object>> results = queryEngine.query(words);
                res.type("application/json");
                return results; // Convert to JSON format if necessary
            } catch (QueryEngineException e) {
                res.status(500);
                return "Error processing query: " + e.getMessage();
            }
        });

        System.out.println("Query engine running on http://localhost:8080/search?q=<word>");
    }
}
