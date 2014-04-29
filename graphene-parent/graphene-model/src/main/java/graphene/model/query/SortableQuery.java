package graphene.model.query;

public interface SortableQuery {

	/**
	 * @return the sortColumn
	 */
	public abstract String getSortColumn();

	/**
	 * @param sortColumn
	 *            the sortColumn to set
	 */
	public abstract void setSortColumn(String sortColumn);

	/**
	 * @return the sortAscending
	 */
	public abstract boolean isSortAscending();

	/**
	 * @param sortAscending
	 *            the sortAscending to set
	 */
	public abstract void setSortAscending(boolean sortAscending);

}