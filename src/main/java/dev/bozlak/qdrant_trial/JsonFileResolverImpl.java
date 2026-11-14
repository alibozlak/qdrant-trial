package dev.bozlak.qdrant_trial;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class JsonFileResolverImpl implements JsonFileResolver {
    
    public static final String BASE_PATH = "./qdrant_results"; 
    public static final String ADDING_PATH = BASE_PATH +  "/adding"; 

    private final Gson gson;

    public JsonFileResolverImpl(Gson gson) {
        this.gson = gson;
    }

    @Override 
    public Map<String, float[]> getPoints() throws Exception {
        String[] fileNames = {
            "ai_news1.json", "astronomy_article.json", "biology.json", "func_trial_politic3.json",
            "geography.json", "literature_turkey.json", "literature_turkey2.json", 
            "math_article.json", "ottoman_history.json", "phys.json", 
            "politics_turkey1.json", "politics_turkey2.json"
        };

        int fileCount = fileNames.length;
        for (int i = 0; i < fileCount; i++)
            fileNames[i] = ADDING_PATH + "/" + fileNames[i];

        return this.resolve(fileNames);
    }

    @Override 
    public Map<String, float[]> resolve(String... jsonFileNames) throws Exception {
        Map<String, float[]> points = new HashMap<>();
        for (String jsonFileName : jsonFileNames) {
            Reader reader = new FileReader(jsonFileName);
            VectorModelForGson vectorModel = this.gson.fromJson(reader, VectorModelForGson.class);
            points.put(jsonFileName, vectorModel.vector);
        }
        return points;
    }

}
