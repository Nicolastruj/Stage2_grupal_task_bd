package org.ulpgc.model;

import org.ulpgc.exceptions.QueryEngineException;

import java.util.List;
import java.util.Map;

public interface QueryEngine {
    List<Map<String, Object>> query(String[] words) throws QueryEngineException;
}
