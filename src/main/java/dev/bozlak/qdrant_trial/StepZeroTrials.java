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
import io.qdrant.client.QueryFactory;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.UpdateResult;

public class StepZeroTrials {

	private final TokenService tokenService;
	private final EmbeddingVectorService embeddingVectorService;

	public StepZeroTrials(TokenService tokenService, EmbeddingVectorService embeddingVectorService) {
		this.tokenService = tokenService;
		this.embeddingVectorService = embeddingVectorService;
	}

	private final String QUERY_ARTICLE = 
		"Jensen Huang her ne kadar zamanında AI'da yavaşlama olmamalı gibi bir kendi şirketine dayalı saçma " + 
		"sapan bir açıklama yapmış olsa da, elindeki VRAM'leri ondan almaya çalışıyorlar. Keywords : " + 
		"Pentagon, Sam Altman, AI Training, Trump, Politics, Politika. " + 
		"Effective çözüm bulamayanların liderlik için alavere dalavereleri.. Siyaset, politika"
	;
	private final String COLLECTION_NAME = "Articles";

	/**
	 * Qdrant useable. Similarity has <b>float point</b> precision 
	 */
	void qdrantDotProductSimilarityTest() throws Exception {
		this.tokenService.validateTokenCount(this.QUERY_ARTICLE);
		float[] queryVector = this.embeddingVectorService.getGeneratedEmbeddingVector(QUERY_ARTICLE);
		QdrantClient qdrantClient = this.createQdrantClient();

		QueryPoints queryPoints = QueryPoints.newBuilder()
			.setCollectionName(COLLECTION_NAME)
			.setQuery(QueryFactory.nearest(queryVector))
			.setLimit(3)
			.setWithPayload(WithPayloadSelectorFactory.enable(true))
			.build();
		List<ScoredPoint> scoredPoints = qdrantClient.queryAsync(queryPoints).get();

		for (ScoredPoint scoredPoint : scoredPoints) 
			System.out.println("ID: " + scoredPoint.getId() + " | Dot Product Score: " + scoredPoint.getScore());

		/*** Qdrant Dot Product Similarity Scores ************* (My dot product scores) ****************************/
		// ID: uuid: "8c1dd8b2-80a7-4d3a-bbf0-d530de6f958d" (politics_turkey2.json : 0.4076422020133492 ---------- 1)
		// | Dot Product Score: 0.4076422
		// ID: uuid: "41e0add7-65d0-4be1-aba7-356726968254" (ai_news1.json : 0.39085296997217256 ----------------- 2)
		// | Dot Product Score: 0.390853
		// ID: uuid: "b0846ed8-ceed-4076-b137-328560f8bbf8" (politics_turkey1.json : 0.37209325964482787 --------- 3)
		// | Dot Product Score: 0.37209326
	}

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
    
    void trials(QdrantAndEmbeddingModelTrial qdrantAndEmbeddingModelTrialFunc) throws Exception {
		QdrantClient qdrantClient = this.createQdrantClient();

		// addPointsTrials();
		qdrantAndEmbeddingModelTrialFunc.doSomething(qdrantClient);

		qdrantClient.close();
	}

	void addPointsTrials() throws Exception {
		// checkCollectionExist(collectionName, qdrantClient);

		String article = this.QUERY_ARTICLE;
		String key = "func_trial_politic";
		
		addPointToQdrant(article, key, this.createQdrantClient(), COLLECTION_NAME);
	}

	void addPointToQdrant(
		String realData, 
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

	private QdrantClient createQdrantClient() {
		return new QdrantClient(QdrantGrpcClient.newBuilder("localhost", 6334, false).build());
	}
}
