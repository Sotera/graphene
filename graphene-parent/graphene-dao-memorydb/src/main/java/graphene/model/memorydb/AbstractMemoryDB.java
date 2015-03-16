package graphene.model.memorydb;

import graphene.dao.EntityRefDAO;
import graphene.dao.IdTypeDAO;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idlhelper.PropertyMatchDescriptorHelper;
import graphene.model.query.G_CallBack;
import graphene.model.query.SearchFilter;
import graphene.model.view.CustomerDetails;
import graphene.model.view.entities.IdType;
import graphene.util.FastNumberUtils;
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

public abstract class AbstractMemoryDB<T, I> implements G_CallBack, IMemoryDB<T, I, CustomerDetails> {
	protected static MemIndex accounts;
	protected static int currentRow;

	protected static MemIndex customers;

	protected static MemRow[] grid;
	protected static MemIndex identifiers;
	private static Logger logger = LoggerFactory.getLogger(AbstractMemoryDB.class);
	// private static long nRows;
	private static final int STATE_LOAD_GRID = 2;
	protected static final int STATE_LOAD_STRINGS = 1;
	protected Set<String> accountSet;

	protected Set<String> customerSet;

	@Inject
	private final EntityRefDAO<T> dao;
	@Inject
	private G_NodeTypeAccess nodeTypeAccess;
	@Inject
	private G_PropertyKeyTypeAccess propertyKeyTypeAccess;
	@Inject
	private G_EdgeTypeAccess edgeTypeAccess;

	protected boolean enabled = false;

	protected Set<String> identifierSet;

	@Inject
	protected IdTypeDAO<?> idTypeDAO;

	protected HashMap<Integer, Integer> invalidTypes = new HashMap<Integer, Integer>(10);

	private boolean loaded;
	protected long numProcessed = 0;
	protected int state;

	public AbstractMemoryDB(final EntityRefDAO<T> dao, final IdTypeDAO<?> idTypeDAO) {
		this.dao = dao;
		this.idTypeDAO = idTypeDAO;
	}

	@Override
	public List<CustomerDetails> customersForIdentifier(final String identifier, final String family,
			final boolean rowPerAccount) {
		final Set<String> customersFound = new HashSet<String>();
		final List<CustomerDetails> results = new ArrayList<CustomerDetails>();

		final Set<MemRow> dbResults = getRowsForIdentifier(identifier);
		final Set<String> accounts = new HashSet<String>();
		G_IdType nodeType;
		try {
			nodeType = nodeTypeAccess.getNodeType(family);

			// G_CanonicalPropertyType canonicalFamily = G_CanonicalPropertyType
			// .fromValue(family);

			for (final MemRow r : dbResults) {
				if (idTypeDAO.getByType(r.getIdType()).getShortName().equals(nodeType.getName())) {
					// So we need this identifier
					final String custno = getCustomerNumberForID(r.entries[CUSTOMER]);
					customersFound.add(custno);
				}
			}
			// customersFound is now a unique set of customer nbrs matching the
			// search

			for (final String custno : customersFound) {
				final CustomerDetails c = new CustomerDetails(custno);
				c.setMatchString(identifier);
				accounts.clear();
				for (final MemRow crow : getRowsForCustomer(custno)) {
					final IdType type = idTypeDAO.getByType(crow.getIdType());
					c.addIdentifier(type, getIdValueForID(crow.entries[IDENTIFIER]));
					if (rowPerAccount) {
						accounts.add(getIdValueForID(crow.entries[ACCOUNT]));
					} else {
						c.addAccount(getIdValueForID(crow.entries[ACCOUNT]));
					}
				}
				if (rowPerAccount) {
					if (accounts.size() == 0) {
						results.add(c);
					} else {
						for (final String ac : accounts) {
							c.getAccountSet().clear();
							c.addAccount(ac);
							results.add(c);
						}
					}
				} else {
					results.add(c);
				}

			}
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return results;
	}

	@Override
	public Set<String> entityIDsByAdvancedSearch(final G_EntityQuery srch) {
		boolean found = false;
		final Set<String> results = new HashSet<String>();
		final Set<String> pastResults = new HashSet<String>();

		final List<G_PropertyMatchDescriptor> descriptors = srch.getPropertyMatchDescriptors();
		for (final G_PropertyMatchDescriptor d : descriptors) {
			if (d.getConstraint().equals(G_Constraint.REQUIRED_EQUALS)) {
				results.addAll(exactMatch(d));
			}
		}
		// if (filters.size() == 1) {
		// final SearchFilter f = srch.getFilters().get(0);
		// if (f.getCompareType() == G_SearchType.COMPARE_EQUALS) {
		// return exactMatch(f);
		// }
		// }
		// TODO: can improve by doing the exact match first if it is
		// one of multiple filters
		String val;
		int pass = 0;
		final Long limit = srch.getMaxResult();
		int numberFound = 0;
		for (final SearchFilter f : filters) {
			pass++;
			logger.trace("About to scan grid with filter " + f);
			results.clear();

			for (final MemRow r : grid) {
				if (r == null) {
					break; // TODO: find out why we are getting a null and
							// whether it means we are at the end
				}
				if ((pass > 1) && (!pastResults.contains(getCustomerNumberForID(r.entries[CUSTOMER])))) {
					continue; // This is not the first pass and not found in
								// earlier pass
				}
				val = getIdValueForID(r.entries[IDENTIFIER]);
				if (val == null) {
					continue;
				}
				final String family = idTypeDAO.getNodeType(r.getIdType());
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
					logger.info("There were more results than will be returned.  Only returning " + limit);
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
	public Set<String> exactMatch(final G_PropertyMatchDescriptor d) {

		final Set<String> results = new HashSet<String>();
		Set<MemRow> rows;
		final PropertyMatchDescriptorHelper pmdh = PropertyMatchDescriptorHelper.from(d);
		int identifier = 0;
		if (pmdh.getType().equals(G_PropertyType.STRING)) {
			identifier = FastNumberUtils.parseIntWithCheck((String) pmdh.getValue());
		} else if (pmdh.getType().equals(G_PropertyType.LONG)) {
			identifier = (int) pmdh.getValue();
		}
		rows = getRowsForIdentifier(identifier);

		for (final MemRow r : rows) {
			if (idTypeDAO.getNodeType(r.getIdType()).equalsIgnoreCase(d.getKey())) {
				logger.trace("Family for row : " + idTypeDAO.getNodeType(r.getIdType()));
				logger.trace("Field name for search " + d.getKey());
				final String cno = getCustomerNumberForID(r.entries[CUSTOMER]);
				results.add(cno);
			}
		}
		return results;
	}

	@Override
	public Set<String> findRegexMatches(String srchValue, final String family, final boolean caseSensitive) {
		int flags;
		if (caseSensitive) {
			flags = 0;
		} else {
			srchValue = srchValue.toLowerCase();
			flags = Pattern.CASE_INSENSITIVE;
		}
		logger.trace("Compiling regex pattern " + srchValue);

		final Pattern p = Pattern.compile(srchValue, flags);
		Matcher ms;

		final Set<String> results = new HashSet<String>();
		for (final String s : identifiers.getValues()) {
			ms = p.matcher(s);
			if (ms.find(0) && isIdFamily(s, family)) {
				results.add(s);
			}
		}
		return results;
	}

	@Override
	public Set<MemRow> findRegexRows(final String srchValue, final String family, final boolean caseSensitive) {
		final Set<String> values = findRegexMatches(srchValue, family, caseSensitive);
		final Set<MemRow> results = new HashSet<MemRow>();
		for (final String s : values) {
			results.addAll(getRowsForIdentifier(s));
		}
		return results;
	}

	@Override
	public Set<String> findSoundsLikeMatches(final String name, final String family) {
		final DoubleMetaphone dm = new DoubleMetaphone();
		String dmcomp = null;
		final Set<String> results = new HashSet<String>();
		try {
			dmcomp = (String) dm.encode((Object) name);
		} catch (final EncoderException e) {
			logger.error(e.getMessage());
			return results;
		}
		for (final String s : identifiers.getValues()) {
			if (dm.encode(s).equals(dmcomp)) {
				results.add(s);
			}
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
	protected int fixLinks(final MemRow[] grid, final String value, final MemIndex index, final int offset) {
		final int id = index.getIDForValue(value);
		if (id < 0) {
			logger.error("Invalid id for value '" + value + "'");
			return 0;
		}

		final int idstart = index.getHeads()[id];

		if (idstart == -1) {
			// this is the first time
			index.getHeads()[id] = currentRow;
		} else {
			// this is not the first time
			final MemRow lastrow = grid[index.getTails()[id]];
			lastrow.nextrows[offset] = currentRow;
		}
		index.getTails()[id] = currentRow;

		return id;

	}

	@Override
	public int getAccountIDForNumber(final String number) {
		return accounts.getIDForValue(number);
	}

	@Override
	public String getAccountNumberForID(final int id) {
		return accounts.getValueForID(id);
	}

	@Override
	public int getCustomerIDForNumber(final String number) {
		return customers.getIDForValue(number);
	}

	@Override
	public String getCustomerNumberForID(final int id) {
		return customers.getValueForID(id);
	}

	@Override
	public int getIdentifierIDForValue(final String value) {
		return identifiers.getIDForValue(value);
	}

	@Override
	public String getIdValueForID(final int id) {
		return identifiers.getValueForID(id);
	}

	@Override
	public Set<MemRow> getRowsForAccount(final int id) {
		return traverse(id, accounts, ACCOUNT);
	}

	@Override
	public Set<MemRow> getRowsForAccount(final String ac) {
		final int id = accounts.getIDForValue(ac);
		if (id < 0) {
			logger.warn("Invalid id for account '" + ac + "'");
			return new HashSet<MemRow>();
		}

		return traverse(id, accounts, ACCOUNT);
	}

	@Override
	public Set<MemRow> getRowsForCustomer(final int custno) {
		return traverse(custno, customers, CUSTOMER);
	}

	@Override
	public Set<MemRow> getRowsForCustomer(final String cust) {
		final int id = customers.getIDForValue(cust);
		// new block --djue
		if (id < 0) {
			logger.warn("Invalid id for customer '" + cust + "'");
			return new HashSet<MemRow>();
		}
		return traverse(id, customers, CUSTOMER);
	}

	@Override
	public Set<MemRow> getRowsForIdentifier(final int id) {
		return traverse(id, identifiers, IDENTIFIER);
	}

	@Override
	public Set<MemRow> getRowsForIdentifier(final String ident) {
		final int id = identifiers.getIDForValue(ident);
		// new block --djue
		if (id < 0) {
			logger.warn("Invalid id for identifier '" + ident + "'");
			return new HashSet<MemRow>();
		}
		return traverse(id, identifiers, IDENTIFIER);

	}

	@Override
	public Set<MemRow> getRowsForIdentifier(final String ident, final String family) {
		final int id = identifiers.getIDForValue(ident);
		Set<MemRow> setOfRows;
		if ((family == null) || (family.length() == 0) || family.equalsIgnoreCase("all")
				|| family.equalsIgnoreCase("any")) {
			setOfRows = traverse(id, identifiers, IDENTIFIER);
		} else {
			setOfRows = traverseWithFamily(id, identifiers, family, IDENTIFIER);
		}
		return setOfRows;
	}

	@Override
	public Set<String> getValuesContaining(final Set<String> vals, final boolean caseSensitive) {
		return identifiers.findValuesContaining(vals, caseSensitive);
	}

	@Override
	public void initialize(final int maxRecords) {
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
		final TimeReporter t = new TimeReporter("Initializing MemoryDB", logger);
		final MemoryReporter m = new MemoryReporter("Reading from database...", logger);
		final boolean success = loadStrings(maxRecords);

		if (success) {
			test(100000);
			if (numProcessed == 0) {
				logger.error("There were no rows to load. ");
			} else {

				// Only make an array for the total number of rows we will
				// actually load
				logger.debug("Creating an array of memrows with size " + numProcessed);
				grid = new MemRow[(int) numProcessed];
				final boolean successOnLoadGrid = loadGrid(maxRecords);

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
	public boolean isIdFamily(final String value, final String family) {
		if ((family == null) || (family.length() == 0) || family.equalsIgnoreCase("all")
				|| family.equalsIgnoreCase("any")) {
			return true;
		} else {
			for (final MemRow r : getRowsForIdentifier(value)) {
				if (idTypeDAO.getNodeType(r.getIdType()).equalsIgnoreCase(family)) {
					return true;
				}
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
	private boolean loadGrid(final int maxRecords) {
		JVMHelper.getFreeMem();
		if (maxRecords > 0) {
			logger.debug("Loading Grid, up to " + maxRecords + " records.");
		} else {
			logger.debug("Loading Grid, for all available records.");
		}
		final TimeReporter t = new TimeReporter("loadGrid", logger);
		final MemoryReporter m = new MemoryReporter("loadGrid", logger);
		currentRow = 0;
		state = STATE_LOAD_GRID;
		numProcessed = 0;
		final G_EntityQuery q = G_EntityQuery.newBuilder().setAttributeList(null).build();
		final boolean loadGridSuccessful = dao.performCallback(0, maxRecords, this, q);
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
	private boolean loadStrings(final int maxRecords) {
		JVMHelper.getFreeMem();
		final TimeReporter t = new TimeReporter("loadStrings", logger);
		final MemoryReporter m = new MemoryReporter("loadStrings", logger);
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
		final boolean loadStringsSuccessful = dao.performCallback(0, maxRecords, this, null);
		if (!loadStringsSuccessful) {
			logger.error("Unsuccessful attempt to populate MemoryDB: callback unsuccessful for loadStrings");
		} else {
			final String[] idArray = identifierSet.toArray(new String[identifierSet.size()]);
			final String[] customerArray = customerSet.toArray(new String[customerSet.size()]);
			final String[] accountArray = accountSet.toArray(new String[accountSet.size()]);

			identifierSet = null;
			customerSet = null;
			accountSet = null;

			identifiers.load(idArray);
			customers.load(customerArray);
			accounts.load(accountArray);

			logger.debug("Loaded " + customers.getCount() + " unique customer numbers");
			logger.debug("Loaded " + accounts.getCount() + " unique account numbers");
			logger.debug("Loaded " + identifiers.getCount() + " unique identifiers");
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
			for (final Integer i : invalidTypes.keySet()) {

				logger.warn("Invalid type " + i + " encounted " + invalidTypes.get(i) + " times.");
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
	private List<CustomerDetails> matchToCustomers(final String family, final Set<String> values) {
		logger.trace("matchToCustomers with " + values.size() + " values");
		final Set<String> customersFound = new HashSet<String>();
		final List<CustomerDetails> results = new ArrayList<CustomerDetails>();

		for (final String s : values) {
			final List<CustomerDetails> custs = customersForIdentifier(s, family, false);
			for (final CustomerDetails c : custs) {
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
	void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void setLoaded(final boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public Set<MemRow> soundsLikeSearch(final String src, final String family) {
		final DoubleMetaphone dm = new DoubleMetaphone();
		final Set<String> matches = new HashSet<String>();
		final Set<MemRow> results = new HashSet<MemRow>();
		String dmcomp = null;

		try {
			dmcomp = (String) dm.encode((Object) src);
		} catch (final EncoderException e) {
			logger.error(e.getMessage());
			return results;
		}
		for (final String s : identifiers.getValues()) {
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
		for (final MemRow i : ids) {
			logger.trace(identifiers.getValueForID(i.entries[CUSTOMER]));
		}

		ids = getRowsForCustomer(7);
		for (final MemRow i : ids) {
			logger.trace(identifiers.getValueForID(i.entries[CUSTOMER]));
		}

	}

	public boolean test(final int ntests) {
		if (identifiers.getCount() > 0) {
			final int[] tests = new int[ntests];
			final Random r = new Random();
			for (int i = 0; i < ntests; ++i) {
				tests[i] = r.nextInt(identifiers.getCount() - 1);
			}
			final TimeReporter t = new TimeReporter("Testing in-memory db", logger);
			for (int i = 0; i < ntests; ++i) {
				final int n = tests[i];
				final String c = identifiers.getValueForID(n);
				final int offset = identifiers.getIDForValue(c);
				if (offset != n) {
					return false;
				}
			}

			t.logAverageTime(ntests);

			return true;
		} else {
			logger.warn("Test will fail because no identifiers were loaded.");
			return false;
		}
	}

	private Set<MemRow> traverse(final int id, final MemIndex index, final int col) {
		final Set<MemRow> results = new HashSet<MemRow>();
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

	private Set<MemRow> traverseWithFamily(final int id, final MemIndex index, final String family, final int col) {
		final Set<MemRow> results = new HashSet<MemRow>();
		int row;

		if (id >= 0) {
			row = index.getHeads()[id];
			while (row >= 0) {
				final MemRow r = grid[row];
				if (idTypeDAO.getNodeType(r.getIdType()).equalsIgnoreCase(family)) {
					results.add(grid[row]);
					row = grid[row].nextrows[col];
				}
			}
		}
		return results;
	}

}