package graphene.dao;

import graphene.model.query.BasicQuery;
import graphene.model.query.EventQuery;

import java.util.List;

/**
 * We need to pick the term transfer or transaction, having the two is too
 * confusing. I think transaction is gaining ground, and supports two sided
 * operations, so this one I'm marking deprecated.--djue
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
@Deprecated
public interface TransferDAO<T, Q extends BasicQuery> extends GenericDAO<T, Q> {
	/**
	 * FIXME:Move this to it's own DAO for finding mappings between accounts.
	 * The more generic version will be a key to federating search across many
	 * datsets.
	 * 
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public abstract List<String> getRelatedAccounts(EventQuery q)
			throws Exception;

}