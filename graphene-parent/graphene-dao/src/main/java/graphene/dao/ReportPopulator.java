package graphene.dao;

import java.util.Set;

import graphene.model.idl.G_Report;

/**
 * Populates a G_Report
 * 
 * This was created to allow the CombinedEntitySearchPage to be moved into core.
 * This will encapsulate the customer specific business logic that that page
 * used to hold.
 * 
 * 
 * @param <T>
 *            The object you are creating a report from.
 * @param <Q>
 *            Anything to do with the query, which may affect highlighting,
 *            ranking, metrics, icons, etc.
 */
public interface ReportPopulator<T, Q> {
	public G_Report populate(T entity, Q s);

	Set<String> getCIdentifiers(Object currentEntity);

	Set<String> getIdentifiers(Object currentEntity);

	Set<String> getDates(Object currentEntity);

	Set<String> getNames(Object currentEntity);

	Set<String> getIcons(Object currentEntity, String searchValue);

	String getReportId(Object currentEntity);

	String getReportType(Object currentEntity);

	Set<String> getAddresses(Object currentEntity);

	String getAmount(Object currentEntity);

	String getPageLink(Object currentEntity);
}
