package graphene.model.memorydb;

import graphene.dao.EntityRefDAO;
import graphene.dao.IdTypeDAO;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_SearchType;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.SearchFilter;
import graphene.model.view.entities.CustomerDetails;
import graphene.model.view.entities.IdType;
import graphene.util.G_CallBack;
import graphene.util.jvm.JVMHelper;
import graphene.util.stats.MemoryReporter;
import graphene.util.stats.TimeReporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avro.AvroRemoteException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMemoryDB<T, I, EntityQuery> implements G_CallBack<T, EntityQuery>,
		IMemoryDB<T, I, CustomerDetails> {
	protected static MemIndex accounts;
	protected static int currentRow;

	protected static MemIndex customers;

	protected static MemRow[] grid;
	protected static MemIndex identifiers;
	private static Logger logger = LoggerFactory
			.getLogger(AbstractMemoryDB.class);
	// private static long nRows;
	private static final int STATE_LOAD_GRID = 2;
	protected static final int STATE_LOAD_STRINGS = 1;
	protected Set<String> accountSet;

	protected Set<String> customerSet;

	@Inject
	private EntityRefDAO<T> dao;
	@Inject
	private G_NodeTypeAccess nodeTypeAccess;
	@Inject
	private G_PropertyKeyTypeAccess propertyKeyTypeAccess;
	@Inject
	private G_EdgeTypeAccess edgeTypeAccess;

	protected boolean enabled = false;

	protected Set<String> identifierSet;

	@Inject
	protected IdTypeDAO<?, ?> idTypeDAO;

	protected HashMap<Integer, Integer> invalidTypes = new HashMap<Integer, Integer>(
			10);

	private boolean loaded;
	protected long numProcessed = 0;
	protected int state;

	public AbstractMemoryDB(EntityRefDAO<T> dao, IdTypeDAO<?, ?> idTypeDAO) {
		this.dao = dao;
		this.idTypeDAO = idTypeDAO;
	}

	public abstract boolean callBack(T p);

	public abstract boolean callBack(T p, EntityQuery q);

	@Override
	public List<CustomerDetails> customersForIdentifier(String identifier,
			String family, boolean rowPerAccount) {
		Set<String> customersFound = new HashSet<String>();
		List<CustomerDetails> results = new ArrayList<CustomerDetails>();

		Set<MemRow> dbResults = getRowsForIdentifier(identifier);
		Set<String> accounts = new HashSet<String>();
		G_IdType nodeType;
		try {
			nodeType = nodeTypeAccess.getNodeType(family);

			// G_CanonicalPropertyType canonicalFamily = G_CanonicalPropertyType
			// .fromValue(family);

			for (MemRow r : dbResults) {
				if (idTypeDAO.getByType(r.getIdType()).getShortName()
						.equals(nodeType.getName())) {
					// So we need this identifier
					String custno = getCustomerNumberForID(r.entries[CUSTOMER]);
					customersFound.add(custno);
				}
			}
			// customersFound is now a unique set of customer nbrs matching the
			// search

			for (String custno : customersFound) {
				CustomerDetails c = new CustomerDetails(custno);
				c.setMatchString(identifier);
				accounts.clear();
				for (MemRow crow : getRowsForCustomer(custno)) {
					IdType type = idTypeDAO.getByType(crow.getIdType());
					c.addIdentifier(type,
							getIdValueForID(crow.entries[IDENTIFIER]));
					if (rowPerAccount)
						accounts.add(getIdValueForID(crow.entries[ACCOUNT]));
					else
						c.addAccount(getIdValueForID(crow.entries[ACCOUNT]));
				}
				if (rowPerAccount) {
					if (accounts.size() == 0)
						results.add(c);
					else
						for (String ac : accounts) {
							c.getAccountSet().clear();
							c.addAccount(ac);
							results.add(c);
						}
				} else {
					results.add(c);
				}

			}
		} catch (AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return results;
	}

	@Override
	public Set<String> entityIDsByAdvancedSearch(AdvancedSearch srch) {
		boolean found = false;

		Set<String> results = new HashSet<String>();
		Set<String> pastResults = new HashSet<String>();

		List<SearchFilter> filters = srch.getFilters();

		if (filters.size() == 1) {
			SearchFilter f = srch.getFilters().get(0);
			if (f.getCompareType() == G_SearchType.COMPARE_EQUALS) {
				return exactMatch(f);
			}
		}
		// TODO: can improve by doing the exact match first if it is
		// one of multiple filters
		String val;
		int pass = 0;
		int limit = srch.getLimit();
		int numberFound = 0;
		for (SearchFilter f : filters) {
			pass++;
			logger.trace("About to scan grid with filter " + f);
			results.clear();

			for (MemRow r : grid) {
				if (r == null) {
					break; // TODO: find out why we are getting a null and
							// whether it means we are at the end
				}
				if ((pass > 1)
						&& (!pastResults
								.contains(getCustomerNumberForID(r.entries[CUSTOMER])))) {
					continue; // This is not the first pass and not found in
								// earlier pass
				}
				val = getIdValueForID(r.entries[IDENTIFIER]);
				if (val == null) {
					continue;
				}
				String family = idTypeDAO.getNodeType(r.getIdType());
				if (family == null) {
					continue;
				}
				found = f.doCompare(val, family);
				if (found) {
					numberFound++;
					if (numberFound >= srch.getStart()) {
						results.add(getCustomerNumberForID(r.entries[CUSTOMER]));
					}
				}
				if ((results.size() + pastResults.size()) > limit) {
					logger.info("There were more results than will be returned.  Only returning "
							+ limit);
					break;
				}
			} // each row
			logger.debug("Done scan grid with " + results.size() + " results");
			pastResults.clear();
			pastResults.addAll(results);

		}// each filter

		return results;
	}

	@Override
	public Set<String> exactMatch(SearchFilter f) {

		Set<String> results = new HashSet<String>();
		Set<MemRow> rows;

		rows = getRowsForIdentifier(f.getValue());

		for (MemRow r : rows) {
			if (idTypeDAO.getNodeType(r.getIdType()).equalsIgnoreCase(
					f.getFieldName())) {

				logger.trace("Family for row : "
						+ idTypeDAO.getNodeType(r.getIdType()));
				logger.trace("Field name for search " + f.getFieldName());

				String cno = getCustomerNumberForID(r.entries[CUSTOMER]);
				results.add(cno);
			}
		}
		return results;
	}

	@Override
	public Set<String> findRegexMatches(String srchValue, String family,
			boolean caseSensitive) {
		int flags;
		if (caseSensitive) {
			flags = 0;
		} else {
			srchValue = srchValue.toLowerCase();
			flags = Pattern.CASE_INSENSITIVE;
		}
		logger.trace("Compiling regex pattern " + srchValue);

		Pattern p = Pattern.compile(srchValue, flags);
		Matcher ms;

		Set<String> results = new HashSet<String>();
		for (String s : identifiers.getValues()) {
			ms = p.matcher(s);
			if (ms.find(0) && isIdFamily(s, family)) {
				results.add(s);
			}
		}
		return results;
	}

	@Override
	public Set<MemRow> findRegexRows(String srchValue, String family,
			boolean caseSensitive) {
		Set<String> values = findRegexMatches(srchValue, family, caseSensitive);
		Set<MemRow> results = new HashSet<MemRow>();
		for (String s : values)
			results.addAll(getRowsForIdentifier(s));
		return results;
	}

	@Override
	public Set<String> findSoundsLikeMatches(String name, String family) {
		DoubleMetaphone dm = new DoubleMetaphone();
		String dmcomp = null;
		Set<String> results = new HashSet<String>();
		try {
			dmcomp = (String) dm.encode((Object) name);
		} catch (EncoderException e) {
			logger.error(e.getMessage());
			return results;
		}
		for (String s : identifiers.getValues()) {
			if (dm.encode(s).equals(dmcomp))
				results.add(s);
		}
		return results;
	}

	/**
	 * What does this do....
	 * 
	 * @param grid
	 *            an array of MemRow objects
	 * @param value
	 *            an identifierString, a customerNumber or an accountNumber.
	 * @param index
	 * @param offset
	 * @return a valid id or zero
	 */
	protected int fixLinks(MemRow[] grid, String value, MemIndex index,
			int offset) {
		int id = index.getIDForValue(value);
		if (id < 0) {
			logger.error("Invalid id for value '" + value + "'");
			return 0;
		}

		int idstart = index.getHeads()[id];

		if (idstart == -1) {
			// this is the first time
			index.getHeads()[id] = currentRow;
		} else {
			// this is not the first time
			MemRow lastrow = grid[index.getTails()[id]];
			lastrow.nextrows[offset] = currentRow;
		}
		index.getTails()[id] = currentRow;

		return id;

	}

	@Override
	public int getAccountIDForNumber(String number) {
		return accounts.getIDForValue(number);
	}

	@Override
	public String getAccountNumberForID(int id) {
		return accounts.getValueForID(id);
	}

	@Override
	public int getCustomerIDForNumber(String number) {
		return customers.getIDForValue(number);
	}

	@Override
	public String getCustomerNumberForID(int id) {
		return customers.getValueForID(id);
	}

	@Override
	public int getIdentifierIDForValue(String value) {
		return identifiers.getIDForValue(value);
	}

	@Override
	public String getIdValueForID(int id) {
		return identifiers.getValueForID(id);
	}

	@Override
	public Set<MemRow> getRowsForAccount(int id) {
		return traverse(id, accounts, ACCOUNT);
	}

	@Override
	public Set<MemRow> getRowsForAccount(String ac) {
		int id = accounts.getIDForValue(ac);
		if (id < 0) {
			logger.warn("Invalid id for account '" + ac + "'");
			return new HashSet<MemRow>();
		}

		return traverse(id, accounts, ACCOUNT);
	}

	@Override
	public Set<MemRow> getRowsForCustomer(int custno) {
		return traverse(custno, customers, CUSTOMER);
	}

	@Override
	public Set<MemRow> getRowsForCustomer(String cust) {
		int id = customers.getIDForValue(cust);
		// new block --djue
		if (id < 0) {
			logger.warn("Invalid id for customer '" + cust + "'");
			return new HashSet<MemRow>();
		}
		return traverse(id, customers, CUSTOMER);
	}

	@Override
	public Set<MemRow> getRowsForIdentifier(int id) {
		return traverse(id, identifiers, IDENTIFIER);
	}

	@Override
	public Set<MemRow> getRowsForIdentifier(String ident) {
		int id = identifiers.getIDForValue(ident);
		// new block --djue
		if (id < 0) {
			logger.warn("Invalid id for identifier '" + ident + "'");
			return new HashSet<MemRow>();
		}
		return traverse(id, identifiers, IDENTIFIER);

	}

	@Override
	public Set<MemRow> getRowsForIdentifier(String ident, String family) {
		int id = identifiers.getIDForValue(ident);
		Set<MemRow> setOfRows;
		if (family == null || family.length() == 0
				|| family.equalsIgnoreCase("all")
				|| family.equalsIgnoreCase("any")) {
			setOfRows = traverse(id, identifiers, IDENTIFIER);
		} else {
			setOfRows = traverseWithFamily(id, identifiers, family, IDENTIFIER);
		}
		return setOfRows;
	}

	@Override
	public Set<String> getValuesContaining(Set<String> vals,
			boolean caseSensitive) {
		return identifiers.findValuesContaining(vals, caseSensitive);
	}

	@Override
	public void initialize(int maxRecords) {
		if (maxRecords > 0) {
			logger.info("MemoryDB intends to load up to " + maxRecords);
		} else {
			logger.info("MemoryDB intends to load all records");
		}
		/*
		 * How this works: We do two passes through the database. On the first
		 * pass we create sets of unique values for identifier,customer, and
		 * account. Note that SELECT DISTINCT and ORDER BY are case-insensitive,
		 * so we can't use those. We then turn those sets into sorted arrays
		 * that we can use as lookup tables.
		 * 
		 * On the second pass we create an in-memory representation of the
		 * table, substituting numbers for the strings, and including three
		 * linked lists: pointing to the next row for the same identifier,
		 * account, and customer respectively.
		 */
		JVMHelper.getFreeMem();
		TimeReporter t = new TimeReporter("Initializing MemoryDB", logger);
		MemoryReporter m = new MemoryReporter("Reading from database...",
				logger);
		boolean success = loadStrings(maxRecords);

		if (success) {
			test(100000);
			if (numProcessed == 0) {
				logger.error("There were no rows to load. ");
			} else {

				// Only make an array for the total number of rows we will
				// actually load
				logger.debug("Creating an array of memrows with size "
						+ numProcessed);
				grid = new MemRow[(int) numProcessed];
				boolean successOnLoadGrid = loadGrid(maxRecords);

				if (successOnLoadGrid) {
					logger.debug("Time to create and load grid " + t.report());
					loaded = true;
				} else {
					logger.error("There was an error during loading the grid for the MemoryDB.  MemoryDB.loaded will equal false.");
					// just to make sure.
					loaded = false;
				}

			}
		}
		t.logAsCompleted();
		m.logMemoryUsedByEvent("Initialize");
		m.logBytesUsedPerItem("Initialize record", numProcessed);
		logInvalidTypes();
	}

	/**
	 * @return the enabled
	 */
	boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isIdFamily(String value, String family) {
		if (family == null || family.length() == 0
				|| family.equalsIgnoreCase("all")
				|| family.equalsIgnoreCase("any")) {
			return true;
		} else {
			for (MemRow r : getRowsForIdentifier(value)) {
				if (idTypeDAO.getNodeType(r.getIdType()).equalsIgnoreCase(
						family))
					return true;
			}
			return false;
		}
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Stage 2 of 2
	 * 
	 * @param maxRecords
	 * @return
	 */
	private boolean loadGrid(int maxRecords) {
		JVMHelper.getFreeMem();
		if (maxRecords > 0) {
			logger.debug("Loading Grid, up to " + maxRecords + " records.");
		} else {
			logger.debug("Loading Grid, for all available records.");
		}
		TimeReporter t = new TimeReporter("loadGrid", logger);
		MemoryReporter m = new MemoryReporter("loadGrid", logger);
		currentRow = 0;
		state = STATE_LOAD_GRID;
		numProcessed = 0;
		boolean loadGridSuccessful = dao.performCallback(0, maxRecords, (G_CallBack<T, graphene.model.query.EntityQuery>) this,
				null);
		if (!loadGridSuccessful) {
			logger.error("Unsuccessful attempt to populate MemoryDB: callback unsuccessful for loadGrid");
		} else {
			logger.debug("Loaded grid in " + t.report());
		}
		// Free the memory used by the tails, which is only needed for inserts
		identifiers.setTails(customers.setTails(accounts.setTails(null)));
		JVMHelper.getFreeMem();
		m.logMegabytesUsed("load grid");
		m.logMemoryUsedByEvent("load grid");
		m.logBytesUsedPerItem("loadGrid record", numProcessed);
		return loadGridSuccessful;
	}

	/**
	 * Stage 1
	 * 
	 * @param maxRecords
	 * @return
	 */
	private boolean loadStrings(int maxRecords) {
		JVMHelper.getFreeMem();
		TimeReporter t = new TimeReporter("loadStrings", logger);
		MemoryReporter m = new MemoryReporter("loadStrings", logger);
		if (maxRecords > 0) {
			logger.debug("Loading Strings, up to " + maxRecords + " records.");
		} else {
			logger.debug("Loading Strings, for all available records.");
		}
		customers = new MemIndex("customers");
		accounts = new MemIndex("accounts");
		identifiers = new MemIndex("identifiers");

		state = STATE_LOAD_STRINGS;
		identifierSet = new HashSet<String>();
		customerSet = new HashSet<String>();
		accountSet = new HashSet<String>();
		numProcessed = 0;
		boolean loadStringsSuccessful = dao.performCallback(0, maxRecords,
				(G_CallBack<T, graphene.model.query.EntityQuery>) this, null);
		if (!loadStringsSuccessful) {
			logger.error("Unsuccessful attempt to populate MemoryDB: callback unsuccessful for loadStrings");
		} else {
			String[] idArray = (String[]) identifierSet
					.toArray(new String[identifierSet.size()]);
			String[] customerArray = (String[]) customerSet
					.toArray(new String[customerSet.size()]);
			String[] accountArray = (String[]) accountSet
					.toArray(new String[accountSet.size()]);

			identifierSet = null;
			customerSet = null;
			accountSet = null;

			identifiers.load(idArray);
			customers.load(customerArray);
			accounts.load(accountArray);

			logger.debug("Loaded " + customers.getCount()
					+ " unique customer numbers");
			logger.debug("Loaded " + accounts.getCount()
					+ " unique account numbers");
			logger.debug("Loaded " + identifiers.getCount()
					+ " unique identifiers");
		}
		JVMHelper.getFreeMem();
		t.logAsCompleted();
		m.logMegabytesUsed("load strings");
		m.logMemoryUsedByEvent("load strings");
		m.logBytesUsedPerItem("loadString record", numProcessed);
		return loadStringsSuccessful;
	}

	@Override
	public void logInvalidTypes() {
		if (!invalidTypes.isEmpty()) {
			for (Integer i : invalidTypes.keySet()) {

				logger.warn("Invalid type " + i + " encounted "
						+ invalidTypes.get(i) + " times.");
			}
		}
	}

	/**
	 * Takes a list of identifiers and returns a list of customers Has to
	 * preserve the order of the identifiers passed in
	 * 
	 * @param family
	 *            family to match (null means all)
	 * @param values
	 *            List<String> - the identifiers list, ranked by best match
	 *            first
	 * @return a list of CustomerDetails objects
	 */
	private List<CustomerDetails> matchToCustomers(String family,
			Set<String> values) {
		logger.trace("matchToCustomers with " + values.size() + " values");
		Set<String> customersFound = new HashSet<String>();
		List<CustomerDetails> results = new ArrayList<CustomerDetails>();

		for (String s : values) {
			List<CustomerDetails> custs = customersForIdentifier(s, family,
					false);
			for (CustomerDetails c : custs) {
				if (!customersFound.contains(c.getCustomerNumber())) {
					results.add(c);
					customersFound.add(c.getCustomerNumber());
				}
			}
		}
		logger.trace("MatchToCustomers found " + results.size() + " customers");
		return results;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public Set<MemRow> soundsLikeSearch(String src, String family) {
		DoubleMetaphone dm = new DoubleMetaphone();
		Set<String> matches = new HashSet<String>();
		Set<MemRow> results = new HashSet<MemRow>();
		String dmcomp = null;

		try {
			dmcomp = (String) dm.encode((Object) src);
		} catch (EncoderException e) {
			logger.error(e.getMessage());
			return results;
		}
		for (String s : identifiers.getValues()) {
			if (dm.encode(s).equals(dmcomp)) {
				matches.add(s);
				results.addAll(getRowsForIdentifier(s, family));
			}
		}

		return results;
	}

	public void test() {
		Set<MemRow> ids;
		ids = getRowsForCustomer(6);
		for (MemRow i : ids) {
			logger.trace(identifiers.getValueForID(i.entries[CUSTOMER]));
		}

		ids = getRowsForCustomer(7);
		for (MemRow i : ids) {
			logger.trace(identifiers.getValueForID(i.entries[CUSTOMER]));
		}

	}

	public boolean test(int ntests) {
		if (identifiers.getCount() > 0) {
			int[] tests = new int[ntests];
			Random r = new Random();
			for (int i = 0; i < ntests; ++i) {
				tests[i] = r.nextInt(identifiers.getCount() - 1);
			}
			TimeReporter t = new TimeReporter("Testing in-memory db", logger);
			for (int i = 0; i < ntests; ++i) {
				int n = tests[i];
				String c = identifiers.getValueForID(n);
				int offset = identifiers.getIDForValue(c);
				if (offset != n)
					return false;
			}

			t.logAverageTime(ntests);

			return true;
		} else {
			logger.warn("Test will fail because no identifiers were loaded.");
			return false;
		}
	}

	private Set<MemRow> traverse(int id, MemIndex index, int col) {
		Set<MemRow> results = new HashSet<MemRow>();
		int row;

		if (id >= 0) {
			row = index.getHeads()[id];
			while (row >= 0) {
				results.add(grid[row]);
				row = grid[row].nextrows[col];
			}
		}
		return results;
	}

	private Set<MemRow> traverseWithFamily(int id, MemIndex index,
			String family, int col) {
		Set<MemRow> results = new HashSet<MemRow>();
		int row;

		if (id >= 0) {
			row = index.getHeads()[id];
			while (row >= 0) {
				MemRow r = grid[row];
				if (idTypeDAO.getNodeType(r.getIdType()).equalsIgnoreCase(
						family)) {
					results.add(grid[row]);
					row = grid[row].nextrows[col];
				}
			}
		}
		return results;
	}

}