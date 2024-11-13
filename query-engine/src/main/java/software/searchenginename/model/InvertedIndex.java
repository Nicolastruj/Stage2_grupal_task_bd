package software.searchenginename.model;

import java.util.List;

public interface InvertedIndex {
    List<Entry> get(String word);
}
