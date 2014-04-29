package graphene.model.idl;

import org.neo4j.graphdb.Label;

/**
 * Sampling from several open source datasets
 * @author djue
 *
 */
//Model facts as nodes, verbs as relationships
public enum G_NodeType implements Label {
	ACCOUNT,
	GROUP,
	ANONYMOUS,
	UNKNOWN,
	ENTITY,
	NAME,
	CUSTOMER_NUMBER,
	PHONE_NUMBER,
	SUBSCRIBER_ID,
	PHYSICAL_ID,
	COMMUNICATION_EVENT,
	EMAIL_ADDRESS,
	EMAIL_CONTENTS,
	ADDRESS,
	ATTRIBUTE,
	EVENT,
	NATIONAL_ID,
	UNIQUE_ID,
	UNIQUE_ACCOUNT_ID,
	COMMUNICATION_END_POINT,
	REDACTED;
}
