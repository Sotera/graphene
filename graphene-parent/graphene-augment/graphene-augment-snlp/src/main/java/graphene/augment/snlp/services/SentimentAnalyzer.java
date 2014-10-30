package graphene.augment.snlp.services;

import graphene.augment.snlp.model.StringWithSentiment;

public interface SentimentAnalyzer {

	public abstract StringWithSentiment findSentiment(String line);

}