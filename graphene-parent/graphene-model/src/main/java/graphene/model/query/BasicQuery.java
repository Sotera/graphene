/**
 * 
 */
package graphene.model.query;

import graphene.util.DisplayUtil;

import org.joda.time.DateTime;

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
	private long firstResult = 0;
	/*
	 * Used for logging and persistence purposes
	 */
	private String id = null;
	private long maxResult = 0;
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

	private String sortField;

	private long timeInitiated = DateTime.now().getMillis();

	private String userId = "None";

	private String userName = "None";

	/**
	 * 
	 */
	public BasicQuery() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BasicQuery other = (BasicQuery) obj;
		if (dataSource == null) {
			if (other.dataSource != null) {
				return false;
			}
		} else if (!dataSource.equals(other.dataSource)) {
			return false;
		}
		if (firstResult != other.firstResult) {
			return false;
		}
		if (maxResult != other.maxResult) {
			return false;
		}
		if (maxSecs != other.maxSecs) {
			return false;
		}
		if (minSecs != other.minSecs) {
			return false;
		}
		if (schema == null) {
			if (other.schema != null) {
				return false;
			}
		} else if (!schema.equals(other.schema)) {
			return false;
		}
		if (sortAscending != other.sortAscending) {
			return false;
		}
		if (sortColumn == null) {
			if (other.sortColumn != null) {
				return false;
			}
		} else if (!sortColumn.equals(other.sortColumn)) {
			return false;
		}
		if (sortField == null) {
			if (other.sortField != null) {
				return false;
			}
		} else if (!sortField.equals(other.sortField)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		if (userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!userName.equals(other.userName)) {
			return false;
		}
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
	public long getFirstResult() {
		// TODO Auto-generated method stub
		return firstResult;
	}

	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.PageableQuery#getMaxResult()
	 */
	@Override
	public long getMaxResult() {
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
	public final String getSortField() {
		return sortField;
	}

	public long getTimeInitiated() {
		return timeInitiated;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((dataSource == null) ? 0 : dataSource.hashCode());
		result = (prime * result) + (int) (firstResult ^ (firstResult >>> 32));
		result = (prime * result) + (int) (maxResult ^ (maxResult >>> 32));
		result = (prime * result) + (int) (maxSecs ^ (maxSecs >>> 32));
		result = (prime * result) + (int) (minSecs ^ (minSecs >>> 32));
		result = (prime * result) + ((schema == null) ? 0 : schema.hashCode());
		result = (prime * result) + (sortAscending ? 1231 : 1237);
		result = (prime * result)
				+ ((sortColumn == null) ? 0 : sortColumn.hashCode());
		result = (prime * result)
				+ ((sortField == null) ? 0 : sortField.hashCode());
		result = (prime * result) + ((userId == null) ? 0 : userId.hashCode());
		result = (prime * result)
				+ ((userName == null) ? 0 : userName.hashCode());
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

	public void setDataSource(final String dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.PageableQuery#setFirstResult(int)
	 */
	@Override
	public void setFirstResult(final long firstResult) {
		this.firstResult = firstResult;
	}

	public void setId(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.PageableQuery#setMaxResult(int)
	 */
	@Override
	public void setMaxResult(final long maxResult) {
		this.maxResult = maxResult;
	}

	/**
	 * @param maxSecs
	 *            the maxSecs to set
	 */
	public final void setMaxSecs(final long maxSecs) {
		this.maxSecs = maxSecs;
	}

	/**
	 * @param minSecs
	 *            the minSecs to set
	 */
	public final void setMinSecs(final long minSecs) {
		this.minSecs = minSecs;
	}

	public void setSchema(final String schema) {
		this.schema = schema;
	}

	public void setSortAndDirection(final String sortColumnWithDirection) {
		if (sortColumnWithDirection.contains(DisplayUtil.DESCENDING_FLAG)) {
			sortAscending = false;
		} else {
			sortAscending = true; // MFM
		}
		sortColumn = sortColumnWithDirection.replace(
				DisplayUtil.DESCENDING_FLAG, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.SortableQuery#setSortAscending(boolean)
	 */
	@Override
	public void setSortAscending(final boolean sortAscending) {
		this.sortAscending = sortAscending;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.common.SortableQuery#setSortColumn(java.lang
	 * .String)
	 */
	/**
	 * You should use setSortAndDirection() instead.
	 */
	@Override
	@Deprecated
	public void setSortColumn(final String sortColumn) {
		this.sortColumn = sortColumn;
	}

	/**
	 * @param sortField
	 *            the sortField to set
	 */
	public final void setSortField(final String sortField) {
		this.sortField = sortField;
	}

	public void setTimeInitiated(final long timeInitiated) {
		this.timeInitiated = timeInitiated;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "BasicQuery [dataSource=" + dataSource + ", firstResult="
				+ firstResult + ", maxResult=" + maxResult + ", maxSecs="
				+ maxSecs + ", minSecs=" + minSecs + ", schema=" + schema
				+ ", sortAscending=" + sortAscending + ", sortColumn="
				+ sortColumn + ", sortField=" + sortField + ", userId="
				+ userId + ", userName=" + userName + "]";
	}
}