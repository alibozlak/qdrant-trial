package dev.bozlak.qdrant_trial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import dev.bozlak.rag.retrieval.TokenService;
import dev.bozlak.rag.retrieval.backends.EmbeddingVectorService;


@SpringBootApplication
public class QdrantTrialApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(QdrantTrialApplication.class, args);


		ConsumerWithException<ApplicationContext> operations = context -> {
			TokenService tokenService = context.getBean(TokenService.class);
			EmbeddingVectorService embeddingVectorService = context.getBean(EmbeddingVectorService.class);

			StepZeroTrials stepZeroTrials = new StepZeroTrials(tokenService, embeddingVectorService);
			// stepZeroTrials.trials(context, stepZeroTrials::addPointsTrials);
			// stepZeroTrials.dotProductSimilarityTest(context);   // <---- Results are better than my predicts :)
			
			// stepZeroTrials.qdrantDotProductSimilarityTest();	   // <---- Good news :)
		};
		wrapOperations(operations);
	}

	static void wrapOperations(ConsumerWithException<ApplicationContext> operations) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("dev.bozlak.qdrant_trial");
		operations.accept(context);
		context.close();
	} 

}
