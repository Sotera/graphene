package graphene.model.query;

import graphene.model.idl.G_SearchTuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityQuery extends BasicQuery {
	private List<G_SearchTuple<String>> attributeList = new ArrayList<G_SearchTuple<String>>(
			1);
	private List<String> filters = new ArrayList<String>(1);
	private boolean caseSensitive = false;
	private boolean searchFreeText = false;
	/**
	 * For backend which allow a minimum relevance score for finding related
	 * results, use this field
	 */
	private double minimumScore = 0.25d;

	private String initiatorId;

	public void addAttribute(final Collection<G_SearchTuple<String>> attr) {
		attributeList.addAll(attr);
	}

	public void addAttribute(final G_SearchTuple<String> attr) {
		attributeList.add(attr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EntityQuery other = (EntityQuery) obj;
		if (attributeList == null) {
			if (other.attributeList != null) {
				return false;
			}
		} else if (!attributeList.equals(other.attributeList)) {
			return false;
		}
		if (caseSensitive != other.caseSensitive) {
			return false;
		}
		if (filters == null) {
			if (other.filters != null) {
				return false;
			}
		} else if (!filters.equals(other.filters)) {
			return false;
		}
		if (initiatorId == null) {
			if (other.initiatorId != null) {
				return false;
			}
		} else if (!initiatorId.equals(other.initiatorId)) {
			return false;
		}
		if (Double.doubleToLongBits(minimumScore) != Double
				.doubleToLongBits(other.minimumScore)) {
			return false;
		}
		if (searchFreeText != other.searchFreeText) {
			return false;
		}
		return true;
	}

	/**
	 * @return the attributeList
	 */
	public final List<G_SearchTuple<String>> getAttributeList() {
		return attributeList;
	}

	public final String[] getAttributeValues() {
		final List<String> list = new ArrayList<String>(1);
		for (final G_SearchTuple<String> a : attributeList) {
			list.add(a.getValue());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * @return the filters
	 */
	public List<String> getFilters() {
		return filters;
	}

	public String getInitiatorId() {
		return initiatorId;
	}

	/**
	 * @return the minimumScore
	 */
	public double getMinimumScore() {
		return minimumScore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result)
				+ ((attributeList == null) ? 0 : attributeList.hashCode());
		result = (prime * result) + (caseSensitive ? 1231 : 1237);
		result = (prime * result)
				+ ((filters == null) ? 0 : filters.hashCode());
		result = (prime * result)
				+ ((initiatorId == null) ? 0 : initiatorId.hashCode());
		long temp;
		temp = Double.doubleToLongBits(minimumScore);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		result = (prime * result) + (searchFreeText ? 1231 : 1237);
		return result;
	}

	/**
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @return the searchFreeText
	 */
	public boolean isSearchFreeText() {
		return searchFreeText;
	}

	public void setAttributeList(final List<G_SearchTuple<String>> attributeList) {
		this.attributeList = attributeList;
	}

	/**
	 * @param caseSensitive
	 *            the caseSensitive to set
	 */
	public void setCaseSensitive(final boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @param filters
	 *            the filters to set
	 */
	public void setFilters(final List<String> filters) {
		this.filters = filters;
	}

	public void setInitiatorId(final String id) {
		initiatorId = id;
	}

	/**
	 * @param minimumScore
	 *            the minimumScore to set
	 */
	public void setMinimumScore(final double minimumScore) {
		this.minimumScore = minimumScore;
	}

	/**
	 * @param searchFreeText
	 *            the searchFreeText to set
	 */
	public void setSearchFreeText(final boolean searchFreeText) {
		this.searchFreeText = searchFreeText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EntityQuery [attributeList=" + attributeList + ", filters="
				+ filters + ", caseSensitive=" + caseSensitive
				+ ", searchFreeText=" + searchFreeText + ", minimumScore="
				+ minimumScore + ", initiatorId=" + initiatorId + "]";
	}

}
