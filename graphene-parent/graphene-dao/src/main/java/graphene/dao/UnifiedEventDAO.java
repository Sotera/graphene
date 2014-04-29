package graphene.dao;

import graphene.model.idl.G_Link;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * For use with tables that have a from and a to id.
 * 
 * @author djue
 * 
 */
public interface UnifiedEventDAO<S, Q> extends GenericDAO<S, Q> {

	public HashMap<String, ArrayList<String>> getRelatedEntities(Q q);

	public HashMap<String, ArrayList<G_Link>> getRelatedLinks(Q q);
}
