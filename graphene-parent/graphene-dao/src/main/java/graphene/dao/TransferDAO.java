package graphene.dao;

import graphene.model.query.EventQuery;

import java.util.List;

public interface TransferDAO<T, Q> extends GenericDAO<T, Q> {
	/**
	 * FIXME:Move this to it's own DAO for finding mappings between accounts.
	 * The more generic version will be a key to federating search across many
	 * datsets.
	 * 
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public abstract List<String> getRelatedAccounts(EventQuery q) throws Exception;


}