package graphene.model.query;

import java.util.ArrayList;
import java.util.List;

public class EntityRefQuery extends BasicQuery {
	private List<EntitySearchTuple<String>> attributeList = new ArrayList<EntitySearchTuple<String>>(
			1);

	private boolean caseSensitive = false;
	private boolean customerQueryFlag = true;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityRefQuery other = (EntityRefQuery) obj;
		if (attributeList == null) {
			if (other.attributeList != null)
				return false;
		} else if (!attributeList.equals(other.attributeList))
			return false;
		if (caseSensitive != other.caseSensitive)
			return false;
		if (customerQueryFlag != other.customerQueryFlag)
			return false;
		return true;
	}

	public List<EntitySearchTuple<String>> getAttributeList() {
		return attributeList;
	}

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

	public void setAttributeList(List<EntitySearchTuple<String>> attributeList) {
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

	@Override
	public String toString() {
		return "EntityRefQuery [attributeList=" + attributeList
				+ ", caseSensitive=" + caseSensitive + ", customerQueryFlag="
				+ customerQueryFlag + "]";
	}

}
