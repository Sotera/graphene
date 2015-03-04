package graphene.model.memorydb;

import graphene.model.query.AdvancedSearch;
import graphene.model.query.SearchFilter;

import java.util.List;
import java.util.Set;

public interface IMemoryDB<T, IDTYPE, CUSTOMERDETAILS> {
	/**
	 * 
	 */
	int ACCOUNT = 1;
	/**
	 * 
	 */
	int CUSTOMER = 2;
	/**
	 * 
	 */
	int IDENTIFIER = 0;

	/**
	 * 
	 * @param p
	 * @return
	 */
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
	List<CUSTOMERDETAILS> customersForIdentifier(String identifier,
			String family, boolean rowPerAccount);

	/**
	 * 
	 * @param srch
	 *            AdvancedSearch object
	 * @return Set<String> of customer Numbers (entity IDs)
	 */
	Set<String> entityIDsByAdvancedSearch(AdvancedSearch srch);

	/**
	 * 
	 * @param f
	 * @return
	 */
	Set<String> exactMatch(SearchFilter f);

	/**
	 * 
	 * @param srchValue
	 * @param family
	 * @param caseSensitive
	 * @return
	 */
	Set<String> findRegexMatches(String srchValue, String family,
			boolean caseSensitive);

	/**
	 * 
	 * @param srchValue
	 * @param family
	 * @param caseSensitive
	 * @return
	 */
	Set<MemRow> findRegexRows(String srchValue, String family,
			boolean caseSensitive);

	/**
	 * 
	 * @param name
	 * @param family
	 * @return
	 */
	Set<String> findSoundsLikeMatches(String name, String family);

	/**
	 * 
	 * @param id
	 * @return
	 */
	Set<MemRow> getRowsForAccount(int id);

	/**
	 * 
	 * @param ac
	 * @return
	 */
	Set<MemRow> getRowsForAccount(String ac);

	/**
	 * 
	 * @param custno
	 * @return
	 */
	Set<MemRow> getRowsForCustomer(int custno);

	/**
	 * 
	 * @param cust
	 * @return
	 */
	Set<MemRow> getRowsForCustomer(String cust);

	/**
	 * 
	 * @param id
	 * @return
	 */
	Set<MemRow> getRowsForIdentifier(int id);

	/**
	 * 
	 * @param ident
	 * @return
	 */
	Set<MemRow> getRowsForIdentifier(String ident);

	/**
	 * TODO: change from string to G_CanonicalPropertyType
	 * 
	 * @param ident
	 * @param family
	 * @return
	 */
	Set<MemRow> getRowsForIdentifier(String ident, String family);

	/**
	 * 
	 * @param vals
	 * @param caseSensitive
	 * @return
	 */
	Set<String> getValuesContaining(Set<String> vals, boolean caseSensitive);

	/**
 * 
 */
	void logInvalidTypes();

	/**
	 * 
	 * @param maxRecords
	 */
	void initialize(int maxRecords);

	/**
	 * XXX:This seems like a huge performance problem if abused.
	 * 
	 * @param value
	 * @param family
	 * @return
	 */
	public abstract boolean isIdFamily(String value, String family);

	/**
	 * 
	 * @return
	 */
	public abstract boolean isLoaded();

	/**
	 * 
	 * @param loaded
	 */
	public abstract void setLoaded(boolean loaded);

	/**
	 * 
	 * @param src
	 * @param family
	 * @return
	 */
	public abstract Set<MemRow> soundsLikeSearch(String src, String family);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public abstract String getIdValueForID(int id);

	/**
	 * 
	 * @param value
	 * @return
	 */
	public abstract int getIdentifierIDForValue(String value);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public abstract String getCustomerNumberForID(int id);

	/**
	 * 
	 * @param number
	 * @return
	 */
	public abstract int getCustomerIDForNumber(String number);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public abstract String getAccountNumberForID(int id);

	/**
	 * 
	 * @param number
	 * @return
	 */
	public abstract int getAccountIDForNumber(String number);

}