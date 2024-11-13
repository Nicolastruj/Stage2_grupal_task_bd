package software.searchenginename.ports;

import software.searchenginename.model.Entry;

public interface Output {
    Pane add(String text);

    interface Pane {
        void add(Entry entry);
    }
}
