package graphene.model.idl;

/**
 * This is kind of the inverse of PropertyTag, which was used as an attribute
 * for influent data properties (and expected no further value)
 * 
 * Here we specify a canonical set of property types which are intended to have
 * values (sometimes just a boolean, sometimes a number or String) These will be
 * applied as properties to Neo4J nodes and edges.
 * 
 * This is a sampling of property types taken from several open source datasets,
 * and is meant to be extended if needed in order to work with the customer
 * specific data. (Although it's a lot less work if you can find one in this
 * list)
 * 
 * In 3.x this would have been a string value that applied for all tuples in a
 * query. Because families are known outside of any particular data, we can
 * treat them as canonical types.
 * 
 * @since 4.0 This enum has been merged with Family from 3.x
 * 
 * @author djue
 * 
 */
public enum G_CanonicalPropertyType {
	ACCOUNT(10, "Account", "account"),
	ACCOUNT_CLASS(11, "Account Class", "accountClass"),
	ACCOUNT_TYPE(12, "Account Type", "accountType"),
	REPORT_ID(15, "Report Id", "reportId"),
	ADDRESS(2, "Address", "address"),
	GEO(21, "Geolocation", "geo"),
	EXPORTADDRESS(23, "Export Address", "export"),
	IMPORTADDRESS(24, "Import Address", "import"),
	ANY(0, "Any", "any"),
	COMBO(3, "Combination", "combo"),
	CONFIRMED(300, "Confirmed", "confirmed"),
	CONTEXT(400, "Context", "context"),
	CORPORATE_CUSTOMER(401, "Corporate Customer", "corporateCustomer"),
	CURRENCY(402, "Currency", "currency"),
	CUSTOMER_CLASS(403, "Customer Class", "customerClass"),
	CUSTOMER_NUMBER(403, "Customer Number", "customerNumber"),
	CUSTOMER_TYPE(404, "Customer Type", "customerType"),
	DECEASED(407, "Deceased", "deceased"),
	DESCRIPTION(408, "Description", "description"),
	EMAIL(4, "Email", "email"),
	IP(41, "IP", "ip"),
	FAMILYROLE(409, "Family Role", "familyRole"),
	ID(5, "Id", "ID"),
	IMPORTANCE(411, "Importance", "importance"),
	JSONVALUE(414, "JSON", "json"),
	LINK(415, "Link", "link"),
	METRIC_CERTAINTY(100, "Certainty", "certainty"),
	METRIC_IMPUTED(412, "Imputed", "imputed"),
	METRIC_IMPUTEDFROM(413, "Imputed From", "imputedFrom"),
	METRIC_PROVENANCE(420, "Provenance", "provenance"),
	METRIC_SCORE(423, "Score", "score"),
	NAME(6, "Name", "name"),
	NODETYPE(417, "Node Type", "nodeType"),
	OCCUPATION(418, "Occupation", "occupation"),
	OTHER(7, "Other", "other"),
	PASSPORT(8, "Passport", "passport"),
	TAXID(81, "Tax Id", "taxId"),
	EIN(82, "EIN", "ein"),
	SSN(83, "SSN", "ssn"),
	GOVERNMENTID(84, "Government Id", "governmentId"),
	VISA(85, "Visa", "visa"),
	LICENSEPLATE(86, "License Plate", "licensePlate"),
	VIN(87, "VIN", "vin"),
	FLIGHT(87, "Airline or Ship", "flight"),
	PHONE(9, "Phone", "phone"),
	PROVIDER_COMPANY_NAME(421, "Provider Name", "providerName"),
	PROVIDER_INDUSTRY(422, "Provider Industry", "providerIndustry"),
	SEX(424, "Sex", "sex"),
	SYSTEMNAME(425, "System Name", "systemName"),
	TIME_CLOSING_DATE(200, "Closing Date", "closingDate"),
	TIME_DATE(405, "Date", "date"),
	TIME_DAY(406, "Day", "day"),
	TIME_HOUR(410, "Hour", "hour"),
	TIME_MONTH(416, "Month", "month"),
	TIME_OPENING_DATE(419, "Opening Date", "openingDate"),
	TIME_YEAR(428, "Year", "year"),
	VALUE(426, "Value", "value"),
	VIP(427, "VIP", "vip"),
	WALLET_ADDRESS(428, "Wallet Address", "walletAddress"),
	USERNAME(429, "Username", "username"),
	ENTITY(500, "Entity", "entity"),
	REDACTED(-1, "[REDACTED]", "REDACTED");

	// For increased performance
	public static G_CanonicalPropertyType[] values = G_CanonicalPropertyType
			.values();

	public static G_CanonicalPropertyType fromIndex(int v) {
		for (G_CanonicalPropertyType c : values) {
			if (c.index == v) {
				return c;
			}
		}
		return ANY;
	}

	/**
	 * Used by IdType DAO to register the types in the database (v) to a
	 * Canonical type.
	 * 
	 * @param v
	 * @return
	 */
	public static G_CanonicalPropertyType fromValue(String v) {
		if (null == v || v.isEmpty()) {
			return ANY;
		}
		for (G_CanonicalPropertyType c : values) {
			if (c.getValueString().equalsIgnoreCase(v)) {
				return c;
			}
		}
		return ANY;
	}

	private String friendlyName;

	private int index;

	private String valueString;

	G_CanonicalPropertyType(int index, String friendlyName, String valueString) {
		this.setIndex(index);
		this.setFriendlyName(friendlyName);

		this.setValueString(valueString);
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public int getIndex() {
		return index;
	}

	public String getValueString() {
		return valueString;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}
}
