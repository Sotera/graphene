package graphene.dao.neo4j;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;

public class GrapheneNeo4JConstants {
	public static final String ACCOUNT_INDEX = "account_index";
	public static final String ACCOUNT_NUMBER = "account_number";
	public static final String CUSTOMER_INDEX = "customer_index";
	public static final String CUSTOMER_NUMBER = "customer_number";
	public static final String IDENTIFIER = "identifier";
	public static final String IDENTIFIER_INDEX = "identifier_index";
	public static final String CUSTOMER_TYPE = "customer_type";
	public static final String CUSTOMER_DATE_END = "customer_date_end";
	public static final String CUSTOMER_DATE_START = "customer_date_start";
	public static final String CUSTOMER_ACCOUNT_RINDEX = "customer_account_rindex";
	public static final String CUSTOMER_ATTRIBUTE_RINDEX = "customer_attribute_rindex";
	public static final Label userLabel = DynamicLabel.label("GUser");

	public static final Label workspaceLabel = DynamicLabel.label("GWorkspace");

	public static final Label groupLabel = DynamicLabel.label("GGroup");
}
