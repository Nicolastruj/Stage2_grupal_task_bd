package software.searchenginename.apps;

import software.searchenginename.control.SearchCommand;
import software.searchenginename.implementations.ApiContext;
import software.searchenginename.implementations.ApiOutput;
import software.searchenginename.implementations.CsvInvertedIndex;

public class MainWithCsvInvertedIndex {
    public static void main(String[] args) {
        SearchCommand searchCommand = new SearchCommand(
                new ApiContext(),
                new CsvInvertedIndex(),
                new ApiOutput()
        );
    }
}
