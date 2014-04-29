package graphene.model.query;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_SearchTuple;

import java.util.ArrayList;
import java.util.List;

public class EntityQuery extends BasicQuery {
	private List<G_SearchTuple<String>> attributeList = new ArrayList<G_SearchTuple<String>>(
			1);

	private G_CanonicalPropertyType family;
	private boolean caseSensitive = false;
	private boolean customerQueryFlag = true;

	/**
	 * Change all settings to their default values.
	 */
	public void clearSettings() {
		attributeList.clear();
		family = null;
		caseSensitive = false;
		customerQueryFlag = true;
	}

	public List<G_SearchTuple<String>> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<G_SearchTuple<String>> attributeList) {
		this.attributeList = attributeList;
	}

	/**
	 * @return the family
	 */
	public G_CanonicalPropertyType getFamily() {
		return family;
	}

	/**
	 * @param family
	 *            the family to set
	 */
	public void setFamily(G_CanonicalPropertyType family) {
		this.family = family;
	}

	/**
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive
	 *            the caseSensitive to set
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return the customerQueryFlag
	 */
	public boolean isCustomerQueryFlag() {
		return customerQueryFlag;
	}

	/**
	 * @param customerQueryFlag
	 *            the customerQueryFlag to set
	 */
	public void setCustomerQueryFlag(boolean customerQueryFlag) {
		this.customerQueryFlag = customerQueryFlag;
	}

}
