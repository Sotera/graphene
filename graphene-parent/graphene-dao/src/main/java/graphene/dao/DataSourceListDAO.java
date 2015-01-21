package graphene.dao;

import graphene.model.datasourcedescriptors.DataSourceList;

import java.util.List;

/**
 * While this is a very simple DAO, if we intend to always pre-load this it may
 * be more readable to have an initialize() method as part of the interface.
 * Implementations that cache the values can use that hook to explicitly fill
 * the values.
 * 
 * OTOH, since the service is a singleton with no mutable state, it can just
 * lazy load without having to 'preload' anything.
 * 
 * @author PWG,djue
 * 
 */
public interface DataSourceListDAO {
	public static final String ALL_REPORTS = "All Reports";

	public List<String> getAvailableTypes();

	public String getDefaultSchema();

	public DataSourceList getList();
}