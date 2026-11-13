package dev.bozlak.qdrant_trial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;

@SpringBootApplication
public class QdrantTrialApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(QdrantTrialApplication.class, args);


		// ********************** Trials *******************************
		QdrantClient qdrantClient = new QdrantClient(
            QdrantGrpcClient.newBuilder("localhost", 6334, false).build()
        );

        String collectionName = "Manifest_Members";
		// checkCollectionExist(collectionName, qdrantClient);

		qdrantClient.grpcClient().collections();
        
        qdrantClient.close();
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
