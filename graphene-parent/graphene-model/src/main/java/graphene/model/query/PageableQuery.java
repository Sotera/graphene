package graphene.model.query;

public interface PageableQuery {

	/**
	 * @return the firstResult
	 */
	public abstract int getFirstResult();

	/**
	 * @return the maxResult
	 */
	public abstract int getMaxResult();

	/**
	 * @param firstResult
	 *            the firstResult to set
	 */
	public abstract void setFirstResult(int firstResult);

	/**
	 * @param maxResult
	 *            the maxResult to set
	 */
	public abstract void setMaxResult(int maxResult);

}