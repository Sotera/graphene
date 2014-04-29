package graphene.model.query;

import java.util.ArrayList;
import java.util.List;

public class EntityRefQuery extends BasicQuery {
	private List<EntitySearchTuple<String>> attributeList = new ArrayList<EntitySearchTuple<String>>(
			1);

	private boolean caseSensitive = false;
	private boolean customerQueryFlag = true;

	public List<EntitySearchTuple<String>> getAttributeList() {
		return attributeList;
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

	public void setAttributeList(
			List<EntitySearchTuple<String>> attributeList) {
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

}
