package graphene.dao.sql;

import graphene.dao.IdTypeDAO;
import graphene.model.idl.G_IdType;
import graphene.model.query.StringQuery;
import graphene.model.view.entities.IdType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public abstract class AbstractIdTypeDAO<T> extends
		GenericDAOJDBCImpl<T, StringQuery> implements IdTypeDAO<T, StringQuery> {
	boolean loaded;
	Map<Integer, IdType> loadedTypes = new HashMap<Integer, IdType>();

	@Inject
	protected Logger logger;

	public AbstractIdTypeDAO() {
		super();
	}

	public boolean applySkipRule(T id) {
		return false;
	}

	public IdType getByType(int typeno) {
		return getLoadedTypes().get(typeno);
	}

	@Override
	public List<Integer> getSkipTypes() {
		// Note, we're calling this here so that skiptypes will be initialized
		// if it was never accessed before.
		// if (loadedTypes == null || loadedTypes.isEmpty()) {
		// logger.debug("Loading types before returning getSkipTypes()");
		// init();
		// }
		return skipTypes;
	}

	List<Integer> skipTypes = new ArrayList<Integer>();

	@Override
	public boolean isBadIdentifier(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLongName(int type) {
		return getColumnSource(type);
	}

	/**
	 * XXX:This is from an old version, remove this.
	 */
	@Override
	public String getColumnSource(int type) {
		if (isLoaded()) {
			String retValue = null;
			try {
				retValue = getLoadedTypes().get(type).getColumnSource();
			} catch (Exception aexp) {
				if (type == 36) {
					retValue = "Address";
				} else {
					retValue = "Other Name";
				}
			}
			return retValue;
		} else {
			return null;
		}
	}

	@Override
	public String getShortName(int type) {
		if (isLoaded()) {
			String retValue = null;
			try {
				retValue = getLoadedTypes().get(type).getShortName();
			} catch (Exception aexp) {
				if (type == 36) {
					retValue = "Address";
				} else {
					retValue = "Other Name";
				}
			}
			return retValue;
		} else {
			return null;
		}
	}

	@Override
	public String getTableSource(int type) {

		return getLoadedTypes().get(type).getTableSource();

	}

	// FIXME: There should be an O(1) way of doing this.
	@Override
	public int getTypeByShortName(String shortName) {
		for (IdType id : getLoadedTypes().values()) {
			if (id.getShortName().equals(shortName)) {
				return id.getIdType_id();
			}
		}
		return 0;
	}

	public IdType getIdTypeByShortName(String shortName) {
		for (IdType id : getLoadedTypes().values()) {
			if (id.getShortName().equals(shortName)) {
				return id;
			}
		}
		return null;
	}

	// FIXME: There should be an O(1) way of doing this.
	@Override
	public Integer[] getTypesForFamily(G_IdType family) {
		List<Integer> results = new ArrayList<Integer>();
		for (IdType s : getLoadedTypes().values()) {
			if (s.getNodeType().equalsIgnoreCase(family.getName()))
				results.add(Integer.valueOf(s.getIdType_id()));
		}
		return results.toArray(new Integer[results.size()]);
	}

	@Override
	public void setLoaded(boolean l) {
		loaded = l;
	}

	@Override
	public void setLoadedTypes(Map<Integer, IdType> lt) {
		loadedTypes = lt;
	}

	@Override
	public void setSkipTypes(List<Integer> skipTypes) {
		this.skipTypes = skipTypes;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void createFamilyMap() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNodeType(int type) {
		String family = "Unknown";
		if (isLoaded()) {
			IdType id = getLoadedTypes().get(type);
			if (id == null) {
				logger.error("IdTypeCache: getNodeType: could not get id definition for type "
						+ type);
			} else {
				family = id.getNodeType();
			}
		}
		return family;

	}

	/**
	 * This public version causes a caching event to occur when it is first
	 * called.
	 */
	@Override
	public Map<Integer, IdType> getLoadedTypes() {
		if (loadedTypes == null || loadedTypes.isEmpty()) {
			logger.debug("Loading types before returning getLoadedTypes()");
			init();
		}
		return loadedTypes;
	}

	@Override
	public void init() {
		logger.debug("Starting initialization");

		try {
			Map<Integer, IdType> typeMap = new HashMap<Integer, IdType>(10);
			List<T> typeList = getAll(0, 0);
			for (T id : typeList) {
				if (applySkipRule(id) == false) {
					IdType idType = convertFrom(id);
					// each idTypeId is unique.
					typeMap.put(idType.getIdType_id(), idType);
				}

			}

			setLoadedTypes(typeMap);
			logger.debug("Will use " + getLoadedTypes().size()
					+ " Type definitions");
			logger.debug(getLoadedTypes().values().toString());
			setLoaded(true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			setLoaded(false);
		}
	}

	/**
	 * This is implemented for each customer implementation. It converts the
	 * local domain object to the standard one.
	 * 
	 * @param id
	 * @return
	 */
	public abstract IdType convertFrom(T id);

}