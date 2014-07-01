package graphene.model.idl;

import org.neo4j.graphdb.RelationshipType;

/**
 * One of the few classes in the core that require Neo4J imports. Because there
 * is no way to extend or subclass RelationshipType at a later time.
 * 
 * Sampling from several open source datasets
 * 
 * @author djue
 * 
 */
public enum G_RelationshipType implements RelationshipType {

	ISA, // ENTITY to def
	KNOWS, // ENTITY to ENTITY
	FRIENDS, // ENTITY to ENTITY
	MARRIED, // ENTITY to ENTITY
	ENEMIES, // ENTITY to ENTITY
	LOVES, // ENTITY to ENTITY
	HATES, // ENTITY to ENTITY
	HAS_ATTRIBUTE, // NamedENTITY to UniqueENTITY
	HAS_EMAIL_ADDRESS, // ENTITY to EMAIL_ADDRESS
	HAS_PHONE, // ENTITY to phone
	HAS_NAME, // ENTITY to name
	HAS_ADDRESS, // ENTITY to address
	FATHER_OF, // ENTITY to ENTITY
	SON_OF, // ENTITY to ENTITY
	MOTHER_OF, // ENTITY to ENTITY
	DAUGHTER_OF, // ENTITY to ENTITY
	FAMILIAL_TIE, // ENTITY to ENTITY
	SUPERSEDES, // n to n_old
	HAS_ID,
	HAS_GOVERNMENT_ID, // GOV to ENTITY
	HAS_KIN,
	MAY_BE_THE_SAME, // n to n
	HAS_GLOBAL_ID,
	MEMBER_OF, // ENTITY to group||company||government
	PART_OF, // ENTITY to group||company||government
	HAS_ACCOUNT, // ENTITY to company
	HAS_PATRONAGE, // ENTITY to company
	HAS_REGISTRATION,
	PLACED_CALL, // ENTITY to ENTITY
	RECEIVED_CALL, // ENTITY to ENTITY
	SENT_MAIL, // ENTITY to ENTITY
	EMAIL_FORWARD_OF, // EMAIL_CONTENTS to EMAIL_CONTENTS
	EMAIL_REPLY_TO, // EMAIL_CONTENTS to EMAIL_CONTENTS
	SENT_EMAIL, // EMAIL_ADDRESS to EMAIL_CONTENTS
	EMAIL_TO, // EMAIL_CONTENTS to EMAIL_ADDRESS
	EMAIL_BCC, // EMAIL_CONTENTS to EMAIL_ADDRESS
	EMAIL_CC, // EMAIL_CONTENTS to EMAIL_ADDRESS
	WORKED_ON,
	WORKS_FOR,
	INTERESTED_IN,
	WORKED_WITH,
	MANAGES,
	PEER_OF,
	ALIAS_OF,
	GUARDIAN_OF,
	RELATED_TO_ID, // use for id to account id (multiple ids)
	IN_SYSTEM, // ACCOUNT TO ENTITY (bank or ENTITY, etc)
	HAS_GLOBAL_ACCOUNT_ID,
	REDACTED,
	IN_DOCUMENT, TRANSACTION,
}
