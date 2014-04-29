package graphene.model.memorydb;

import graphene.model.query.AdvancedSearch;
import graphene.model.query.SearchFilter;

import java.util.List;
import java.util.Set;

public interface IMemoryDB<T, IDTYPE,CUSTOMERDETAILS> {
	public static final int ACCOUNT = 1;
	public static final int CUSTOMER = 2;
	public static final int IDENTIFIER = 0;

	public abstract boolean callBack(T p);

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
	public abstract List<CUSTOMERDETAILS> customersForIdentifier(
			String identifier, String family, boolean rowPerAccount);

	/**
	 * 
	 * @param srch
	 *            AdvancedSearch object
	 * @return Set<String> of customer Numbers (entity IDs)
	 */
	public abstract Set<String> entityIDsByAdvancedSearch(AdvancedSearch srch);

	public abstract Set<String> exactMatch(SearchFilter f);

	/*
	 * public Collection<CustomerDetails> getCustomersForAccount(String account)
	 * { Collection<CustomerDetails> results = new ArrayList<CustomerDetails>();
	 * Set<String> customers = new HashSet<String>(); Set<MemRow> rows =
	 * getRowsForAccount(account); for (MemRow r:rows)
	 * customers.add(r.getCustomerNumber()); for (String c:customers) {
	 * CustomerDetails cust = new CustomerDetails(c); results.add(cust); }
	 * 
	 * return results;
	 * 
	 * }
	 */
	public abstract Set<String> findRegexMatches(String srchValue,
			String family, boolean caseSensitive);

	public abstract Set<MemRow> findRegexRows(String srchValue, String family,
			boolean caseSensitive);

	public abstract Set<String> findSoundsLikeMatches(String name, String family);

	public abstract Set<MemRow> getRowsForAccount(int id);

	public abstract Set<MemRow> getRowsForAccount(String ac);

	public abstract Set<MemRow> getRowsForCustomer(int custno);

	public abstract Set<MemRow> getRowsForCustomer(String cust);

	public abstract Set<MemRow> getRowsForIdentifier(int id);

	public abstract Set<MemRow> getRowsForIdentifier(String ident);

	/**
	 * TODO: change from string to G_CanonicalPropertyType
	 * 
	 * @param ident
	 * @param family
	 * @return
	 */
	public abstract Set<MemRow> getRowsForIdentifier(String ident, String family);

	public abstract Set<String> getValuesContaining(Set<String> vals,
			boolean caseSensitive);

	public abstract void logInvalidTypes();

	public abstract void initialize(int maxRecords);

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