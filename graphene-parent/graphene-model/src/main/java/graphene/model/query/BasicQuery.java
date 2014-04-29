/**
 * 
 */
package graphene.model.query;

import graphene.model.enumeration.ValueEnum;
import graphene.util.DisplayUtil;

/**
 * @author djue
 * 
 *         Developers should extend this class to handle dataset specific query
 *         values. It is unlikely that you would use this class directly, since
 *         you can't instantiate it.
 */
public abstract class BasicQuery implements SortableQuery, PageableQuery {
	/*
	 * Since 4.0
	 * 
	 * For use when multiple datasources are available
	 */
	private String dataSource;
	private int firstResult = 0;

	private int maxResult = 0;

	/*
	 * end date, in seconds since epoch
	 */
	private long maxSecs = 0;

	/*
	 * start date, in seconds since epoch
	 */
	private long minSecs = 0;

	/*
	 * Since 4.0
	 * 
	 * For use when multiple datasources are available
	 */
	private String schema;

	private boolean sortAscending = true;
	private String sortColumn;
	private ValueEnum sortField;
	/**
	 * 
	 */
	public BasicQuery() {
		// TODO Auto-generated constructor stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicQuery other = (BasicQuery) obj;
		if (firstResult != other.firstResult)
			return false;
		if (maxResult != other.maxResult)
			return false;
		if (maxSecs != other.maxSecs)
			return false;
		if (minSecs != other.minSecs)
			return false;
		if (sortAscending != other.sortAscending)
			return false;
		if (sortColumn == null) {
			if (other.sortColumn != null)
				return false;
		} else if (!sortColumn.equals(other.sortColumn))
			return false;
		if (sortField == null) {
			if (other.sortField != null)
				return false;
		} else if (!sortField.equals(other.sortField))
			return false;
		return true;
	}

	public String getDataSource() {
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.PageableQuery#getFirstResult()
	 */
	@Override
	public int getFirstResult() {
		// TODO Auto-generated method stub
		return firstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.PageableQuery#getMaxResult()
	 */
	@Override
	public int getMaxResult() {
		return maxResult;
	}

	/**
	 * @return the maxSecs which is the end of the time period
	 */
	public final long getMaxSecs() {
		return maxSecs;
	}

	/**
	 * @return the minSecs which is the start of the time period
	 */
	public final long getMinSecs() {
		return minSecs;
	}

	public String getSchema() {
		return schema;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.SortableQuery#getSortColumn()
	 */
	@Override
	public String getSortColumn() {
		// TODO Auto-generated method stub
		return sortColumn;
	}

	/**
	 * @return the sortField
	 */
	public final ValueEnum getSortField() {
		return sortField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + firstResult;
		result = prime * result + maxResult;
		result = prime * result + (int) (maxSecs ^ (maxSecs >>> 32));
		result = prime * result + (int) (minSecs ^ (minSecs >>> 32));
		result = prime * result + (sortAscending ? 1231 : 1237);
		result = prime * result
				+ ((sortColumn == null) ? 0 : sortColumn.hashCode());
		result = prime * result
				+ ((sortField == null) ? 0 : sortField.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.SortableQuery#isSortAscending()
	 */
	@Override
	public boolean isSortAscending() {
		// TODO Auto-generated method stub
		return sortAscending;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.PageableQuery#setFirstResult(int)
	 */
	@Override
	public void setFirstResult(int firstResult) {
		// TODO Auto-generated method stub
		this.firstResult = firstResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.PageableQuery#setMaxResult(int)
	 */
	@Override
	public void setMaxResult(int maxResult) {
		// TODO Auto-generated method stub
		this.maxResult = maxResult;
	}

	/**
	 * @param maxSecs
	 *            the maxSecs to set
	 */
	public final void setMaxSecs(long maxSecs) {
		this.maxSecs = maxSecs;
	}

	/**
	 * @param minSecs
	 *            the minSecs to set
	 */
	public final void setMinSecs(long minSecs) {
		this.minSecs = minSecs;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setSortAndDirection(String sortColumnWithDirection) {
		if (sortColumnWithDirection.contains(DisplayUtil.DESCENDING_FLAG)) {
			this.sortAscending = false;
		}
                else {
                    this.sortAscending = true;  // MFM
                }
		sortColumn = sortColumnWithDirection.replace(
				DisplayUtil.DESCENDING_FLAG, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grapheneweb.services.common.SortableQuery#setSortAscending(boolean)
	 */
	@Override
	public void setSortAscending(boolean sortAscending) {
		// TODO Auto-generated method stub
		this.sortAscending = sortAscending;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grapheneweb.services.common.SortableQuery#setSortColumn(java.lang
	 * .String)
	 */
	/**
	 * You should use setSortAndDirection() instead.
	 */
	@Override
	@Deprecated
	public void setSortColumn(String sortColumn) {
		// TODO Auto-generated method stub
		this.sortColumn = sortColumn;
	}

	/**
	 * @param sortField
	 *            the sortField to set
	 */
	public final void setSortField(ValueEnum sortField) {
		this.sortField = sortField;
	}
}