package dev.bozlak.qdrant_trial;

import dev.bozlak.rag.retrieval.EmbeddingModel;

public class SimilarityWithLoop {
    
    public static double dot_product(float[] queryVector, float[] point) {
        double result = 0.;
        int loopSize = EmbeddingModel.getDefaultEmbeddingModel().getModelInfos().embeddingLength;
        for (int i = 0; i < loopSize; i++)
            result += queryVector[i] * point[i];
        return result;
    }
}
