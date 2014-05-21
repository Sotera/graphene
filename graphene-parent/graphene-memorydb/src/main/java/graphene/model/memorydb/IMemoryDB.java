package graphene.model.memorydb;

import graphene.model.query.AdvancedSearch;
import graphene.model.query.SearchFilter;

import java.util.List;
import java.util.Set;

public interface IMemoryDB<T, IDTYPE, CUSTOMERDETAILS> {
	int ACCOUNT = 1;
	int CUSTOMER = 2;
	int IDENTIFIER = 0;

	boolean callBack(T p);

	/**
	 * Takes an identifier and returns a list of customer records
	 * 
	 * @param identifier
	 *            String - the identifier to match
	 * @param family
	 *            String family to match
	 * @param rowPeraccount
	 *            If true, return a row for each of the customer's accounts
	 * @return a list of CustomerDetails objects
	 */
	List<CUSTOMERDETAILS> customersForIdentifier(
			String identifier, String family, boolean rowPerAccount);

	/**
	 * 
	 * @param srch
	 *            AdvancedSearch object
	 * @return Set<String> of customer Numbers (entity IDs)
	 */
	Set<String> entityIDsByAdvancedSearch(AdvancedSearch srch);

	Set<String> exactMatch(SearchFilter f);

	Set<String> findRegexMatches(String srchValue,
			String family, boolean caseSensitive);

	Set<MemRow> findRegexRows(String srchValue, String family,
			boolean caseSensitive);

	Set<String> findSoundsLikeMatches(String name, String family);

	Set<MemRow> getRowsForAccount(int id);

	Set<MemRow> getRowsForAccount(String ac);

	Set<MemRow> getRowsForCustomer(int custno);

	Set<MemRow> getRowsForCustomer(String cust);

	Set<MemRow> getRowsForIdentifier(int id);

	Set<MemRow> getRowsForIdentifier(String ident);

	/**
	 * TODO: change from string to G_CanonicalPropertyType
	 * 
	 * @param ident
	 * @param family
	 * @return
	 */
	Set<MemRow> getRowsForIdentifier(String ident, String family);

	Set<String> getValuesContaining(Set<String> vals,
			boolean caseSensitive);

	void logInvalidTypes();

	void initialize(int maxRecords);

	// XXX:This seems like a huge performance problem if abused.
	public abstract boolean isIdFamily(String value, String family);

	public abstract boolean isLoaded();

	public abstract void setLoaded(boolean loaded);

	public abstract Set<MemRow> soundsLikeSearch(String src, String family);

	public abstract String getIdValueForID(int id);

	public abstract int getIdentifierIDForValue(String value);

	public abstract String getCustomerNumberForID(int id);

	public abstract int getCustomerIDForNumber(String number);

	public abstract String getAccountNumberForID(int id);

	public abstract int getAccountIDForNumber(String number);

}