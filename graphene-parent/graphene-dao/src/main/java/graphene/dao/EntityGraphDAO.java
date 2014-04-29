/**
 * 
 */
package graphene.dao;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_RelationshipType;

import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author djue A DAO base class for identity and relational graphs.
 * 
 */
public interface EntityGraphDAO<T, Q> {
	public List<T> findByQuery(Q q) throws Exception;

	void addRelationship(Node a, Node b, Relationship r, Map<String, Object> payload);

	Relationship findOrCreateUnique(G_RelationshipType type,
			Map<String, Object> payload);

	Node findOrCreateUnique(G_CanonicalPropertyType type,
			Map<String, Object> payload);
}
