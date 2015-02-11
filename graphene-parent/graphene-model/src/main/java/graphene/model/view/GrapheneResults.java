package graphene.model.view;

import java.util.List;

public class GrapheneResults<T> {
	private int numberOfResultsReturned = 0;
	private double maxScore = 0.0d;

	public double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(double maxScore) {
		this.maxScore = maxScore;
	}

	private int numberOfResultsTotal = 0;

	List<T> results;

	/**
	 * @return the numberOfResultsReturned
	 */
	public final int getNumberOfResultsReturned() {
		return numberOfResultsReturned;
	}

	/**
	 * @return the numberOtResultsTotal
	 */
	public final int getNumberOfResultsTotal() {
		return numberOfResultsTotal;
	}

	/**
	 * @return the results
	 */
	public final List<T> getResults() {
		return results;
	}

	/**
	 * @param numberOfResultsReturned
	 *            the numberOfResultsReturned to set
	 */
	public final void setNumberOfResultsReturned(int numberOfResultsReturned) {
		this.numberOfResultsReturned = numberOfResultsReturned;
	}

	/**
	 * @param numberOfResultsTotal
	 *            the numberOtResultsTotal to set
	 */
	public final void setNumberOfResultsTotal(int numberOfResultsTotal) {
		this.numberOfResultsTotal = numberOfResultsTotal;
	}

	/**
	 * @param results
	 *            the results to set
	 */
	public final void setResults(List<T> results) {
		this.results = results;
	}
}
