package graphene.model.graphserver;

import graphene.model.graph.DirectableObject;

import java.util.List;

public interface DirectableObjectLoader<T extends DirectableObject, Q> {
	// setting in q where applicable
	List<T> pairQuery(Q q) throws Exception;

	List<T> getDestFor(String nbr, Q originalQuery) throws Exception;

	List<T> getSrcFor(String nbr, Q originalQuery) throws Exception;

}
