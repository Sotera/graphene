package graphene.model.query;

public interface PageableQuery {

	/**
	 * @return the firstResult
	 */
	public abstract long getFirstResult();

	/**
	 * @return the maxResult
	 */
	public abstract long getMaxResult();

	/**
	 * @param firstResult
	 *            the firstResult to set
	 */
	public abstract void setFirstResult(long firstResult);

	/**
	 * @param maxResult
	 *            the maxResult to set
	 */
	public abstract void setMaxResult(long maxResult);

}