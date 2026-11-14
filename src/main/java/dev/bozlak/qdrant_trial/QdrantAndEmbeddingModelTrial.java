package dev.bozlak.qdrant_trial;

import dev.bozlak.rag.retrieval.TokenService;
import dev.bozlak.rag.retrieval.backends.EmbeddingVectorService;
import io.qdrant.client.QdrantClient;

@FunctionalInterface 
public interface QdrantAndEmbeddingModelTrial {

    /**
     * <b>Vector adding to Qdrant, pulling from same DB,  ..</b> <br>
     * @param qdrantClient
     * @param tokenService
     * @param embeddingVectorService
     * @throws Exception
     */
    void doSomething(
        QdrantClient qdrantClient, TokenService tokenService, EmbeddingVectorService embeddingVectorService
    ) throws Exception;

}
