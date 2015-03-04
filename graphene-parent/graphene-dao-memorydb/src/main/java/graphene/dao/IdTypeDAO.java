package graphene.dao;

import graphene.model.idl.G_IdType;
import graphene.model.query.BasicQuery;
import graphene.model.view.entities.IdType;

import java.util.List;
import java.util.Map;

/**
 * 
 * Each <IdType> corresponds to a single type of Data, for instance Home Address
 * 1. Each IdType also includes a coarse categorization (only one), in this
 * example "Address" Each IdType also include some pedigree information about
 * where the data came from, for instance which datasource and table and column.
 * 
 * Based on this, we can initiate searches based on the specific field, or on
 * the coarse field.
 * 
 * At startup, we load all the unique types into memory, and we also load in the
 * unique coarse types and create a mapping.
 * 
 * 
 * 
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface IdTypeDAO<T, Q extends BasicQuery> extends GenericDAO<T, Q> {

	public abstract void createFamilyMap();

	public abstract IdType getByType(int typeno);

	public abstract String getColumnSource(int type);

	public abstract String getNodeType(int type);

	public abstract Map<Integer, IdType> getLoadedTypes();

	public abstract String getLongName(int type);

	public abstract String getShortName(int type);

	public abstract String getTableSource(int type);

	public abstract int getTypeByShortName(String shortName);

	public abstract IdType getIdTypeByShortName(String shortName);

	public abstract Integer[] getTypesForFamily(G_IdType g_NodeType);

	public abstract boolean isLoaded();

	public abstract void setLoaded(boolean l);

	public abstract void setLoadedTypes(Map<Integer, IdType> lt);

	public abstract String toString();

	public abstract List<Integer> getSkipTypes();

	public abstract void setSkipTypes(List<Integer> skipTypes);

	public abstract void init();

	public boolean isBadIdentifier(String id);

	/**
	 * TOTO: remove the side effect nature of this method for a more graceful
	 * approach Simultaneously check to see if an id should be skipped based on
	 * a rule, and if so, add it to a list.
	 * 
	 * This should be implemented on a per-dataset basis. Create any rules here
	 * on which types to skip, for instance, if the POJO T has a field that
	 * starts with a certain string, etc.
	 * 
	 * @param id
	 * @return true if the item was added to the list of ids to skip. False
	 *         otherwise.
	 */
	public abstract boolean applySkipRule(T id);

}
