package graphene.model.query;

import graphene.model.idl.G_SearchTuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityQuery extends BasicQuery {
	private List<G_SearchTuple<String>> attributeList = new ArrayList<G_SearchTuple<String>>(
			1);

	private boolean caseSensitive = false;
	private boolean customerQueryFlag = true;

	public void addAttribute(Collection<G_SearchTuple<String>> attr) {
		attributeList.addAll(attr);
	}

	public void addAttribute(G_SearchTuple<String> attr) {
		attributeList.add(attr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EntityQuery other = (EntityQuery) obj;
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
		if (customerQueryFlag != other.customerQueryFlag) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((attributeList == null) ? 0 : attributeList.hashCode());
		result = prime * result + (caseSensitive ? 1231 : 1237);
		result = prime * result + (customerQueryFlag ? 1231 : 1237);
		return result;
	}

	/**
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @return the customerQueryFlag
	 */
	public boolean isCustomerQueryFlag() {
		return customerQueryFlag;
	}

	public void setAttributeList(List<G_SearchTuple<String>> attributeList) {
		this.attributeList = attributeList;
	}

	/**
	 * @param caseSensitive
	 *            the caseSensitive to set
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @param customerQueryFlag
	 *            the customerQueryFlag to set
	 */
	public void setCustomerQueryFlag(boolean customerQueryFlag) {
		this.customerQueryFlag = customerQueryFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EntityQuery [");
		if (attributeList != null)
			builder.append("attributeList=").append(attributeList).append(", ");
		builder.append("caseSensitive=").append(caseSensitive)
				.append(", customerQueryFlag=").append(customerQueryFlag)
				.append("]");
		return builder.toString();
	}

}
