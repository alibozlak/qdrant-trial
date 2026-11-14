package dev.bozlak.qdrant_trial;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationContext;

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

public class StepZeroTrials {

	/**
	 * <b>All vectors (contain query vector too) in Qdrant DB</b> <br>
	 * @param context : Application context from Spring Container
	 * @throws Exception : File exceptions
	 */
	void dotProductSimilarityTest(ApplicationContext context) throws Exception {
		Map<String, float[]> points = context.getBean(JsonFileResolver.class).getPoints();
		String queryVectorKey = "./qdrant_results/adding/func_trial_politic3.json";
		System.out.println("Query vector key : " + this.forPrintKey(queryVectorKey));
		float[] queryVector = points.get(queryVectorKey);
		points.remove(queryVectorKey);
		
		System.out.println("------- Dot Product ---------");
		points.keySet().stream().forEach(key -> {
			double dotProduct = SimilarityWithLoop.dot_product(queryVector, points.get(key));
			System.out.println(this.forPrintKey(key) + " : " + dotProduct);
		});
		// Query vector key : func_trial_politic3.json
		/*  Query vector sentence : 
			"Jensen Huang her ne kadar zamanında AI'da yavaşlama olmamalı gibi bir kendi şirketine dayalı saçma " + 
			"sapan bir açıklama yapmış olsa da, elindeki VRAM'leri ondan almaya çalışıyorlar. Keywords : " + 
			"Pentagon, Sam Altman, AI Training, Trump, Politics, Politika. " + 
			"Effective çözüm bulamayanların liderlik için alavere dalavereleri.. Siyaset, politika";
		*/
		// ------- Dot Product ---------
		// literature_turkey2.json : 0.31668179321694545
		// math_article.json : 0.29961422365730783
		// literature_turkey.json : 0.31054839358996844
		// geography.json : 0.3198057204382181
		// politics_turkey1.json : 0.37209325964482787 --------- 3
		// astronomy_article.json : 0.28407144524445016		
		// politics_turkey2.json : 0.4076422020133492 ---------- 1
		// ottoman_history.json : 0.27112466877112595
		// biology.json : 0.3322539500143904
		// phys.json : 0.33494825311353793
		// ai_news1.json : 0.39085296997217256 ----------------- 2

		// Goood news :)
	}
    
    void trials(ApplicationContext context, QdrantAndEmbeddingModelTrial qdrantAndEmbeddingModelTrialFunc) throws Exception {
		QdrantClient qdrantClient = new QdrantClient(
            QdrantGrpcClient.newBuilder("localhost", 6334, false).build()
        );
		
		TokenService tokenService = context.getBean(TokenService.class);
		EmbeddingVectorService embeddingVectorService = context.getBean(EmbeddingVectorService.class);

		// addPointsTrials(qdrantClient, tokenService, embeddingVectorService);
		qdrantAndEmbeddingModelTrialFunc.doSomething(qdrantClient, tokenService, embeddingVectorService);

		qdrantClient.close();
	}

	void addPointsTrials(
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

	void addPointToQdrant(
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

	void checkCollectionExist(String collectionName, QdrantClient qdrantClient) throws Exception {
		if (!qdrantClient.collectionExistsAsync(collectionName).get())
            qdrantClient.createCollectionAsync(collectionName, VectorParams.newBuilder()
                .setSize(dev.bozlak.rag.retrieval.EmbeddingModel.getDefaultEmbeddingModel().getModelInfos().embeddingLength)
                .setDistance(Distance.Dot)
                .build()
            ).get();
	}

	private String forPrintKey(String key) {
		return key.substring(24);
	}
}
