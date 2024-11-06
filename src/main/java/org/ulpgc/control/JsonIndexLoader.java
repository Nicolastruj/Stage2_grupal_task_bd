package org.ulpgc.control;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class JsonIndexLoader { // remake to an Interface ?? 

    public JSONObject loadJsonIndex(String word, String indexFolder) {
        String firstLetter = word.substring(0, 1).toLowerCase();
        String jsonPath = indexFolder + "/indexer_" + firstLetter + ".json";
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(jsonPath)));
            return new JSONObject(content);
        } catch (IOException e) {
            System.out.println("Index file for letter '" + firstLetter + "' not found.");
            return new JSONObject();
        }
    }
}