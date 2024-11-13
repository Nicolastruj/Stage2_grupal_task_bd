package software.searchenginename.control;

import software.searchenginename.model.Entry;
import software.searchenginename.model.InvertedIndex;
import software.searchenginename.ports.Context;
import software.searchenginename.ports.Output;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SearchCommand implements Command {
    private final Context context;
    private final InvertedIndex invertedIndex;
    private final Output output;

    public SearchCommand(Context context, InvertedIndex invertedIndex, Output output) {
        this.context = context;
        this.invertedIndex = invertedIndex;
        this.output = output;
    }

    @Override
    public void execute() {
        displayResults(entriesOf(searchText()));
    }

    private String searchText() {
        return context.getSearchText();
    }

    private Set<Entry> entriesOf(String text) {
        return entriesOf(text.split(" "));
    }

    private Set<Entry> entriesOf(String[] words) {
        Set<Entry> entries = new HashSet<>(invertedIndex.get(words[0]));
        Arrays.stream(words)
                .skip(1)
                .forEach(w->entries.retainAll(invertedIndex.get(w)));
        return entries;
    }

    private void displayResults(Set<Entry> entries) {
        Output.Pane pane = output.add(searchText());
        entries.forEach(pane::add);
    }
}
