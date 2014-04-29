package graphene.dao;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.view.entities.IdType;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface IdTypeDAO<T, Q> extends GenericDAO<T, Q> {

	public abstract void createFamilyMap();

	public abstract IdType getByType(int typeno);

	public abstract String getColumnSource(int type);

	public abstract String getFamily(int type);

	public abstract Map<Integer, IdType> getLoadedTypes();

	public abstract String getLongName(int type);

	public abstract String getShortName(int type);

	public abstract String getTableSource(int type);

	public abstract int getTypeByShortName(String shortName);

	public abstract Integer[] getTypesForFamily(G_CanonicalPropertyType family);

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
