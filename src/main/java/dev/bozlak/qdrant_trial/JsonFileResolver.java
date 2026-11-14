package dev.bozlak.qdrant_trial;

import java.util.Map;

public interface JsonFileResolver {
    
    Map<String, float[]> getPoints() throws Exception;
    Map<String, float[]> resolve(String... jsonFileNames) throws Exception;
}
