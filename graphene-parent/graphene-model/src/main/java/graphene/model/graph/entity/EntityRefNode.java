package graphene.model.graph.entity;

import graphene.model.graph.GenericNode;
import graphene.model.graph.GraphObjectData;
import graphene.model.view.entities.IdType;

/**
 * 
 FIXME: Change static final chars to be defined elsewhere. It's too brittle to
 * store here. --djue
 * 
 * FIXME: This class has extra side effects in some setters.
 * 
 */
public class EntityRefNode extends GenericNode {

	public static final char ENTITY_ACCOUNT = 'A';
	public static final char ENTITY_CUSTOMER = 'C';
	public static final char ENTITY_IDENTIFIER = 'I';

	private String customerNumber = null;

	private IdType id_type = null;

	private boolean scanned = false; // true when we have searched on this value

	public boolean isScanned() {
		return scanned;
	}

	public void setScanned(boolean scanned) {
		this.scanned = scanned;
	}

	private String valueType; // "account" if account else identifier type

	public EntityRefNode() {
		// Used for JAXB - has to have a no-arg constructor
		super();
	}

	public EntityRefNode(char entityType, String value, String id) {
		super();
		this.entityType = entityType;
		this.value = value;
		this.key = entityType + ":" + value;
		this.id = id;
	}

	public void addData(String attribute, String value, String family) {

		super.addData(attribute, value);

		// A customer can have several identifiers with the family of "name".
		// We pick the shortest one for the label

		if ((isCustomer()) && (family.equals("name"))) {
			String oldLabel = getLabel();
			if (oldLabel == null || oldLabel.length() > value.length())
				setLabel(value);
		}

	}

	public void decLinks() {
		--nbrLinks;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public IdType getId_type() {
		return id_type;
	}

	public String getValueType() {
		return valueType;
	}

	public void incLinks() {
		++nbrLinks;
	}

	public boolean isAccount() {
		return entityType == ENTITY_ACCOUNT;
	}

	public boolean isCustomer() {
		return entityType == ENTITY_CUSTOMER;
	}

	public boolean isIdentifier() {
		return entityType == ENTITY_IDENTIFIER;
	}

	public boolean isTraversed() {
		return traversed;
	}

	public void setAccountNumber(String account) {
		addData("Account Number", account);
	}

	public void setAccountNumberType(String type) {
		addData("AccountNumberType", type);
	}

	public void setColors(boolean GQT) {

		String color = NodeColors.getNodeColor(GQT, isOrigin, isLeaf,
				valueType, dataSource);

		if (color != null)
			setBackgroundColor(color);

	}

	public void setCustomerNumber(String nbr) {
		// logger.debug("Setting customer number to " + nbr);
		this.customerNumber = nbr;
		addData("Customer Number", nbr);
	}

	public void setCustomerNumberType(String type) {
		addData("CustomerNumberType ", type);
	}

	public void setExpandable(boolean b) {
		/*
		 * if (!isOrigin) { removeData("node-prop-BackgroundColor");
		 * 
		 * if (b) { // logger.trace("Setting background color");
		 * addData("node-prop-BackgroundColor", "ffff0000"); } }
		 */
	}

	/**
	 * Note, you should probably set the valueType immediately afterwards.
	 * 
	 * @param id_type
	 */
	public void setId_type(IdType id_type) {
		// Only used for Identifier nodes.
		// Note that a person might have the same identifier value for more than
		// one identifier type.
		// So we use the family rather than the exact identifier. They can still
		// see the short name when looking
		// at the linked person node.
		this.id_type = id_type;
	}

	public void setIdTableSource(String source) {
		addData("IdentifierTableSource ", source);
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	@Override
	public void setPlaceholder(boolean isPlaceholder) {
		super.setPlaceholder(isPlaceholder);
		String lab = getLabel();
		if (lab.charAt(0) != '[')
			setLabel("[" + lab + "]");
	}

	public void setTraversed(boolean traversed) {
		this.traversed = traversed;
	}

	@Override
	public void setValue(String value) {
		dataSet.add(new GraphObjectData("Identifier", value));
		this.value = value; // to make traversal easier
	}

	/**
	 * FIXME: Remove the side effects created by the author.
	 * 
	 * @param type
	 */
	public void setValueType(final String type) {

		// Currently one of "account", "customer", or the identifier family
		valueType = type;
		addData("IdentifierType", valueType);

		// String icon = IconMap.getIcon(valueType);
		// setIcon(icon);
		dataSource = 0; // TODO - implement when we have more than one
	}

	@Override
	public String toString() {
		return "Node: " + id + " : " + getKey();
	}

}
