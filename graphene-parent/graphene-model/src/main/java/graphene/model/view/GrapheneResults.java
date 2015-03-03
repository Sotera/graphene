package graphene.model.view;

import java.util.List;

@Deprecated
public class GrapheneResults<T> {
	private int numberOfResultsReturned = 0;
	private double maxScore = 0.0d;

	private int numberOfResultsTotal = 0;

	List<T> results;

	public double getMaxScore() {
		return maxScore;
	}

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

	public void setMaxScore(final double maxScore) {
		this.maxScore = maxScore;
	}

	/**
	 * @param numberOfResultsReturned
	 *            the numberOfResultsReturned to set
	 */
	public final void setNumberOfResultsReturned(final int numberOfResultsReturned) {
		this.numberOfResultsReturned = numberOfResultsReturned;
	}

	/**
	 * @param numberOfResultsTotal
	 *            the numberOtResultsTotal to set
	 */
	public final void setNumberOfResultsTotal(final int numberOfResultsTotal) {
		this.numberOfResultsTotal = numberOfResultsTotal;
	}

	/**
	 * @param results
	 *            the results to set
	 */
	public final void setResults(final List<T> results) {
		this.results = results;
	}
}
