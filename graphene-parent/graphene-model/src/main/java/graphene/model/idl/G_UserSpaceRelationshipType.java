package graphene.model.idl;

import org.neo4j.graphdb.RelationshipType;

public enum G_UserSpaceRelationshipType implements RelationshipType {
	CREATOR_OF, // For user to workspace relations
	EDITOR_OF, // For user to workspace relations
	REVIEWER_OF // For user to workspace relations

}
