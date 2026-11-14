package dev.bozlak.qdrant_trial;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import dev.bozlak.rag.retrieval.TokenService;
import dev.bozlak.rag.retrieval.backends.EmbeddingVectorService;

import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.UpdateResult;

@SpringBootApplication
public class QdrantTrialApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(QdrantTrialApplication.class, args);


		// ********************** Trials *******************************
		// trials(QdrantTrialApplication::addPointsTrials);

		// Dot product vector similarity codes here : 		
	}

	static void trials(QdrantAndEmbeddingModelTrial qdrantAndEmbeddingModelTrialFunc) throws Exception {
		QdrantClient qdrantClient = new QdrantClient(
            QdrantGrpcClient.newBuilder("localhost", 6334, false).build()
        );

		ApplicationContext context = new AnnotationConfigApplicationContext("dev.bozlak.qdrant_trial");
		TokenService tokenService = context.getBean(TokenService.class);
		EmbeddingVectorService embeddingVectorService = context.getBean(EmbeddingVectorService.class);

		// addPointsTrials(qdrantClient, tokenService, embeddingVectorService);
		qdrantAndEmbeddingModelTrialFunc.doSomething(qdrantClient, tokenService, embeddingVectorService);

		qdrantClient.close();
	}

	static void addPointsTrials(
		QdrantClient qdrantClient, TokenService tokenService, EmbeddingVectorService embeddingVectorService
	) throws Exception {
        String collectionName = "Articles";
		// checkCollectionExist(collectionName, qdrantClient);

		String article = "Jensen Huang her ne kadar zamanında AI'da yavaşlama olmamalı gibi bir kendi şirketine dayalı saçma " + 
		"sapan bir açıklama yapmış olsa da, elindeki VRAM'leri ondan almaya çalışıyorlar. Keywords : " + 
		"Pentagon, Sam Altman, AI Training, Trump, Politics, Politika. " + 
		"Effective çözüm bulamayanların liderlik için alavere dalavereleri.. Siyaset, politika";
		String key = "func_trial_politic";
		
		addPointToQdrant(tokenService, article, embeddingVectorService, key, qdrantClient, collectionName);
	}

	static void addPointToQdrant(
		TokenService tokenService, 
		String realData, 
		EmbeddingVectorService embeddingVectorService, 
		String key, 
		QdrantClient qdrantClient, 
		String collectionName
	) throws Exception {
		tokenService.validateTokenCount(realData);

		float[] vectorOfData = embeddingVectorService.getGeneratedEmbeddingVector(realData);

		PointStruct pointStruct = PointStruct.newBuilder()
			.setId(PointIdFactory.id(UUID.randomUUID()))
			.setVectors(VectorsFactory.vectors(vectorOfData))
			.putPayload(key, ValueFactory.value(realData))
			.build();

		UpdateResult result = qdrantClient.upsertAsync(collectionName, List.of(pointStruct)).get();
		System.out.println("Adding result : " + result.getStatus().toString());
	}

	static void checkCollectionExist(String collectionName, QdrantClient qdrantClient) throws Exception {
		if (!qdrantClient.collectionExistsAsync(collectionName).get())
            qdrantClient.createCollectionAsync(collectionName, VectorParams.newBuilder()
                .setSize(dev.bozlak.rag.retrieval.EmbeddingModel.getDefaultEmbeddingModel().getModelInfos().embeddingLength)
                .setDistance(Distance.Dot)
                .build()
            ).get();
	}

}
