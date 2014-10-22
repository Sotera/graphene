package graphene.augment.snlp.model;

import java.util.List;

public class SentimentResult {
	private String keyword;
	private List<StringWithSentiment> sentiments;

	public SentimentResult() {
		// TODO Auto-generated constructor stub
	}
	
	public SentimentResult(String keyword, List<StringWithSentiment> sentiments) {
		super();
		this.keyword = keyword;
		this.sentiments = sentiments;
	}

	/**
	 * @return the keyword
	 */
	public final String getKeyword() {
		return keyword;
	}

	/**
	 * @return the sentiments
	 */
	public final List<StringWithSentiment> getSentiments() {
		return sentiments;
	}

	/**
	 * @param keyword
	 *            the keyword to set
	 */
	public final void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @param sentiments
	 *            the sentiments to set
	 */
	public final void setSentiments(List<StringWithSentiment> sentiments) {
		this.sentiments = sentiments;
	}

}
