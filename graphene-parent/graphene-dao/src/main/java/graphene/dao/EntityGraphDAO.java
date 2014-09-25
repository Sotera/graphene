/**
 * 
 */
package graphene.dao;

import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_IdType;

import java.util.List;
import java.util.Map;

import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericNode;

/**
 * @author djue A DAO base class for identity and relational graphs.
 * 
 */
public interface EntityGraphDAO<T, Q> {
	public List<T> findByQuery(Q q) throws Exception;

	void addRelationship(V_GenericNode a, V_GenericNode b, V_GenericEdge r,
			Map<String, Object> payload);

	V_GenericEdge findOrCreateUnique(G_EdgeType type,
			Map<String, Object> payload);

	V_GenericNode findOrCreateUnique(G_IdType type,
			Map<String, Object> payload);
}
