package dev.bozlak.qdrant_trial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@SpringBootApplication
public class QdrantTrialApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(QdrantTrialApplication.class, args);


		ConsumerWithException<ApplicationContext> operations = context -> {
			StepZeroTrials stepZeroTrials = new StepZeroTrials();
			// stepZeroTrials.trials(context, stepZeroTrials::addPointsTrials);
			// stepZeroTrials.dotProductSimilarityTest(context);   // <---- Results are better than my predicts :)
		};
		wrapOperations(operations);
	}

	static void wrapOperations(ConsumerWithException<ApplicationContext> operations) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("dev.bozlak.qdrant_trial");
		operations.accept(context);
		context.close();
	} 

}
