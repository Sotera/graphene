package graphene.model.query;

import graphene.model.idl.G_SearchTuple;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is used when we have a list of ids which we want to find events for. If
 * intersection is selected, we will only return results where the ID is on
 * all/both sides of an event.
 * 
 * TODO: Reconcile the differences and needs between this class and V_GraphQuery
 * of Vande.
 * 
 * @author djue
 * 
 */
public class EventQuery extends BasicQuery implements IntersectionQuery {
	/*
	 * A string we will look for in the comments
	 */
	private String comments = null;

	/*
	 * If true, we should look for related id(s), and then find all the results
	 * of those id(s).
	 */
	private boolean findRelatedIds = false;

	/*
	 * A list of zero or more ids to filter on. The ids provided might be
	 * related
	 */
	private List<String> idList = new ArrayList<String>();
	private List<G_SearchTuple<String>> attributeList = new ArrayList<G_SearchTuple<String>>(
			1);

	/**
	 * @return the attributeList
	 */
	public List<G_SearchTuple<String>> getAttributeList() {
		return attributeList;
	}

	/**
	 * @param attributeList
	 *            the attributeList to set
	 */
	public void setAttributeList(List<G_SearchTuple<String>> attributeList) {
		this.attributeList = attributeList;
	}

	private boolean intersectionOnly = false;
	/*
	 * The maximum monetary amount (unit not specified)
	 */
	private double maxAmount = 0;

	/*
	 * The minimum monetary amount (unit not specified)
	 */
	private double minAmount = 0;

	@Override
	public String toString() {
		return "EventQuery [comments=" + comments + ", findRelatedIds="
				+ findRelatedIds + ", idList=" + idList + ", intersectionOnly="
				+ intersectionOnly + ", maxAmount=" + maxAmount
				+ ", minAmount=" + minAmount + "]";
	}

	/**
	 * Default constructor
	 */
	public EventQuery() {

	}

	public void addAttribute(
			Collection<? extends G_SearchTuple<String>> attr) {
		attributeList.addAll(attr);
	}

	public void addAttribute(G_SearchTuple<String> attr) {
		attributeList.add(attr);
	}

	/**
	 * Add a single id to the list of ids to search. This is the proper way of
	 * adding ids.
	 * 
	 * @param id
	 */
	public void addId(String id) {
		if (ValidationUtils.isValid(id)) {
			idList.add(id);
		}
	}

	/**
	 * This is the proper, safe way of adding id numbers. Use this instead of
	 * setting the collection, as we filter out bad values.
	 * 
	 * @param ids
	 */
	public void addIds(Collection<String> ids) {
		if (ValidationUtils.isValid(ids)) {
			idList.addAll(ids);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventQuery other = (EventQuery) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (findRelatedIds != other.findRelatedIds)
			return false;
		if (idList == null) {
			if (other.idList != null)
				return false;
		} else if (!idList.equals(other.idList))
			return false;
		if (intersectionOnly != other.intersectionOnly)
			return false;
		if (Double.doubleToLongBits(maxAmount) != Double
				.doubleToLongBits(other.maxAmount))
			return false;
		if (Double.doubleToLongBits(minAmount) != Double
				.doubleToLongBits(other.minAmount))
			return false;
		return true;
	}

	/**
	 * @return the fromdtSecs
	 */
	// public long getFromdtSecs() {
	// return minSecs;
	// }

	public boolean equalsIgnoreLimits(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventQuery other = (EventQuery) obj;
		if (idList == null) {
			if (other.idList != null)
				return false;
		} else if (!idList.equals(other.idList))
			return false;
		if (findRelatedIds != other.findRelatedIds)
			return false;
		if (Double.doubleToLongBits(maxAmount) != Double
				.doubleToLongBits(other.maxAmount))
			return false;
		if (getMaxSecs() != other.getMaxSecs())
			return false;
		if (Double.doubleToLongBits(minAmount) != Double
				.doubleToLongBits(other.minAmount))
			return false;
		if (getMinSecs() != other.getMinSecs())
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (getSortColumn() == null) {
			if (other.getSortColumn() != null)
				return false;
		} else if (!getSortColumn().equals(other.getSortColumn()))
			return false;

		// MFM have to also check the sort direction
		if (isSortAscending() != other.isSortAscending()) {
			return false;
		}
		if (getFirstResult() != other.getFirstResult()) {
			return false;
		}
		return true;
	}

	/**
	 * @return the ids
	 */
	public List<String> getIdList() {
		return idList;
	}

	public String getComments() {
		return comments;
	}

	/**
	 * @return the maxAmount
	 */
	public double getMaxAmount() {
		return maxAmount;
	}

	/**
	 * @return the minAmount
	 */
	public double getMinAmount() {
		return minAmount;
	}

	/**
	 * FIXME: This is some legacy stuff that doesn't apply anymore.
	 * 
	 * @return
	 */
	public String getSingleId() {
		return (String) this.idList.toArray()[0];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + (findRelatedIds ? 1231 : 1237);
		result = prime * result + ((idList == null) ? 0 : idList.hashCode());
		result = prime * result + (intersectionOnly ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(maxAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * @return the findRelatedIds
	 */
	public boolean isFindRelatedIds() {
		return findRelatedIds;
	}

	public boolean isIntersectionOnly() {
		return intersectionOnly;
	}

	/**
	 * FIXME: This is some legacy stuff that doesn't apply anymore. Remove.
	 * 
	 * @return
	 */
	public boolean isSingleId() {
		return idList.size() == 1;
	}

	/**
	 * @param idList
	 *            the list of Ids to set
	 */
	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @param findRelatedIds
	 */
	public void setFindRelatedIds(boolean findRelatedIds) {

		this.findRelatedIds = findRelatedIds;
	}

	public void setIntersectionOnly(boolean intersectionOnly) {
		this.intersectionOnly = intersectionOnly;
	}

	/**
	 * @param maxAmount
	 *            the maxAmount to set
	 */
	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	/**
	 * An alternative setter for parameters given as a String
	 * 
	 * @param maxAmountStr
	 */
	public void setMaxAmount(String maxAmountStr) {
		if (maxAmountStr != null && maxAmountStr.length() > 0)
			maxAmount = Double.parseDouble(maxAmountStr);

	}

	/**
	 * @param minAmount
	 *            the minAmount to set
	 */
	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}

	/**
	 * An alternative setter for parameters given as a String
	 * 
	 * @param minAmountStr
	 */
	public void setMinAmount(String minAmountStr) {
		if (minAmountStr != null && minAmountStr.length() > 0)
			minAmount = Double.parseDouble(minAmountStr);

	}

}
