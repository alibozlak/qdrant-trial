package dev.bozlak.qdrant_trial;

import java.io.IOException;
import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.bozlak.rag.retrieval.TokenService;
import dev.bozlak.rag.retrieval.backends.EmbeddingVectorService;
import dev.bozlak.rag.retrieval.backends.ollama.OllamaEmbeddingVectorServiceImpl;
import dev.bozlak.rag.retrieval.tokenizers.huggingface.HuggingFaceTokenService;
import dev.bozlak.rag.retrieval.tokenizers.huggingface.HuggingFaceTokenizerDefaultConfigForRetrieval;

@Configuration 
public class BeansConfig {
    
    @Bean 
    public Gson gson() {
        return new Gson();
    }

    @Bean 
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean 
    public HuggingFaceTokenizer huggingFaceTokenizer() throws IOException {
        return HuggingFaceTokenizerDefaultConfigForRetrieval.huggingFaceTokenizer();
    }

    @Bean 
    public TokenService tokenService() throws IOException {
        return new HuggingFaceTokenService(this.huggingFaceTokenizer());
    }

    @Bean 
    public EmbeddingVectorService embeddingVectorService() {
        return new OllamaEmbeddingVectorServiceImpl(this.httpClient(), this.gson());
    }

    @Bean 
    public JsonFileResolver jsonFileResolver() {
        return new JsonFileResolverImpl(this.gson());
    }
}
