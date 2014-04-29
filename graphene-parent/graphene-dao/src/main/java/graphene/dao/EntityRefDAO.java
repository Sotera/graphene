package graphene.dao;

import graphene.model.idl.G_SearchType;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EntityRefQuery;

import java.util.List;
import java.util.Set;

/**
 * Interface to permit access to the entityref table through a variety of
 * implementors. <BR>
 * Does not handle the enhanced search options - should only be used for raw
 * access, with an optional second pass to refine <BR>
 * 
 * @author pgofton for DARPA
 * 
 */

public interface EntityRefDAO<T, Q> extends GenericDAO<T, Q> {

	/**
	 * The primary access point for searching the entityref database. <BR/>
	 * Used for the initial search (from web services) and for graph traversal. <BR/>
	 * Making the search values Sets is helpful as we can use "in" parameters to
	 * search multiple values at once <BR>
	 * This does not handle regex, soundalike, or enhanced strings
	 * 
	 * @param q
	 * @return
	 * @throws Exception
	 */
	List<T> rowSearch(EntityRefQuery q) throws Exception;

	public abstract long count() throws Exception;

	Set<String> getAccountsForCustomer(String cust) throws Exception;


	/**
	 * Used to search for unique identifier names and values matching the
	 * search. <BR/>
	 * Search must contain a set of identifier values, and a family. We will
	 * return values that contain *all* the specified values. It is anticipated
	 * that the user will pick one of thes matching values (e.g. a name) for
	 * further searching. <BR/>
	 * Note that the original version of this query used 'like' search types
	 * (aka COMPARE_CONTAINS) for the identifier values.
	 * 
	 * @see G_SearchType
	 * @param q
	 *            A Query object
	 * @return Set<String> of matching values
	 * @throws Exception
	 */

	Set<String> valueSearch(Q q) throws Exception;

	Set<String> regexSearch(String name, String family, boolean caseSensitive);

	Set<String> soundsLikeSearch(String src, String family);

	Set<String> entityIDsByAdvancedSearch(AdvancedSearch srch);

	Set<T> getRowsForCustomer(String cust) throws Exception;

}