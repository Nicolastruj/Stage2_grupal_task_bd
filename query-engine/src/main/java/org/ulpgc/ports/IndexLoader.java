package org.ulpgc.ports;

import org.ulpgc.exceptions.QueryException;

import java.io.IOException;
import java.util.Map;
import java.util.List;

public interface IndexLoader {
    Map<String, List<Integer>> loadWord(String word, String indexFolder) throws IOException, QueryException;
}