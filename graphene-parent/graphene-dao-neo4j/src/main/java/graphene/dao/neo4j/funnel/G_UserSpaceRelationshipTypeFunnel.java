package graphene.dao.neo4j.funnel;

import graphene.model.Funnel;
import graphene.model.idl.G_UserSpaceRelationshipType;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;

public class G_UserSpaceRelationshipTypeFunnel implements
		Funnel<RelationshipType, G_UserSpaceRelationshipType> {

	@Override
	public G_UserSpaceRelationshipType from(RelationshipType f) {
		return G_UserSpaceRelationshipType.valueOf(f.name());
	}

	@Override
	public RelationshipType to(G_UserSpaceRelationshipType f) {
		return DynamicRelationshipType.withName(f.name());
	}
}
