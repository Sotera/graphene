package graphene.dao;

import graphene.dao.sql.GenericDAOJDBCImpl;
import graphene.model.funnels.Funnel;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.view.entities.IdType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public abstract class AbstractIdTypeDAO<T> extends GenericDAOJDBCImpl<T> implements IdTypeDAO<T> {
	boolean loaded;
	Map<Integer, IdType> loadedTypes = new HashMap<Integer, IdType>();
	protected Funnel<G_SearchResult, IdType> funnel;
	@Inject
	protected Logger logger;

	List<Integer> skipTypes = new ArrayList<Integer>();

	public AbstractIdTypeDAO() {
		super();
	}

	@Override
	public boolean applySkipRule(final G_SearchResult id) {
		return false;
	}

	@Override
	public void createFamilyMap() {
		// TODO Auto-generated method stub

	}

	@Override
	public IdType getByType(final int typeno) {
		return getLoadedTypes().get(typeno);
	}

	/**
	 * XXX:This is from an old version, remove this.
	 */
	@Override
	public String getColumnSource(final int type) {
		if (isLoaded()) {
			String retValue = null;
			try {
				retValue = getLoadedTypes().get(type).getColumnSource();
			} catch (final Exception aexp) {
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
	public IdType getIdTypeByShortName(final String shortName) {
		for (final IdType id : getLoadedTypes().values()) {
			if (id.getShortName().equals(shortName)) {
				return id;
			}
		}
		return null;
	}

	/**
	 * This public version causes a caching event to occur when it is first
	 * called.
	 */
	@Override
	public Map<Integer, IdType> getLoadedTypes() {
		if ((loadedTypes == null) || loadedTypes.isEmpty()) {
			logger.debug("Loading types before returning getLoadedTypes()");
			init();
		}
		return loadedTypes;
	}

	@Override
	public String getLongName(final int type) {
		return getColumnSource(type);
	}

	@Override
	public String getNodeType(final int type) {
		String family = "Unknown";
		if (isLoaded()) {
			final IdType id = getLoadedTypes().get(type);
			if (id == null) {
				logger.error("IdTypeCache: getNodeType: could not get id definition for type " + type);
			} else {
				family = id.getNodeType();
			}
		}
		return family;

	}

	@Override
	public String getShortName(final int type) {
		if (isLoaded()) {
			String retValue = null;
			try {
				retValue = getLoadedTypes().get(type).getShortName();
			} catch (final Exception aexp) {
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
	public List<Integer> getSkipTypes() {
		// Note, we're calling this here so that skiptypes will be initialized
		// if it was never accessed before.
		// if (loadedTypes == null || loadedTypes.isEmpty()) {
		// logger.debug("Loading types before returning getSkipTypes()");
		// init();
		// }
		return skipTypes;
	}

	@Override
	public String getTableSource(final int type) {

		return getLoadedTypes().get(type).getTableSource();

	}

	// FIXME: There should be an O(1) way of doing this.
	@Override
	public int getTypeByShortName(final String shortName) {
		for (final IdType id : getLoadedTypes().values()) {
			if (id.getShortName().equals(shortName)) {
				return id.getIdType_id();
			}
		}
		return 0;
	}

	// FIXME: There should be an O(1) way of doing this.
	@Override
	public Integer[] getTypesForFamily(final G_IdType family) {
		final List<Integer> results = new ArrayList<Integer>();
		for (final IdType s : getLoadedTypes().values()) {
			if (s.getNodeType().equalsIgnoreCase(family.getName())) {
				results.add(Integer.valueOf(s.getIdType_id()));
			}
		}
		return results.toArray(new Integer[results.size()]);
	}

	@Override
	public void init() {
		logger.debug("Starting initialization");

		try {
			final Map<Integer, IdType> typeMap = new HashMap<Integer, IdType>(10);
			final G_SearchResults typeList = getAll(0, 0);
			for (final G_SearchResult id : typeList.getResults()) {
				if (applySkipRule(id) == false) {
					final IdType idType = funnel.from(id);
					// each idTypeId is unique.
					typeMap.put(idType.getIdType_id(), idType);
				}

			}

			setLoadedTypes(typeMap);
			logger.debug("Will use " + getLoadedTypes().size() + " Type definitions");
			logger.debug(getLoadedTypes().values().toString());
			setLoaded(true);
		} catch (final Exception e) {
			logger.error(e.getMessage());
			setLoaded(false);
		}
	}

	@Override
	public boolean isBadIdentifier(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void setLoaded(final boolean l) {
		loaded = l;
	}

	@Override
	public void setLoadedTypes(final Map<Integer, IdType> lt) {
		loadedTypes = lt;
	}

	@Override
	public void setSkipTypes(final List<Integer> skipTypes) {
		this.skipTypes = skipTypes;
	}

}