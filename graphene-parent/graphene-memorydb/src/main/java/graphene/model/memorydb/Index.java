package graphene.model.memorydb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UNUSED
 * 
 * Maps each name or address in the database to a set of (customer) numbers
 * Provides a facility to search the names and addresses in memory, which is
 * faster than using the database (especially for regex)
 * 
 * @author pgofton
 * 
 */
public class Index {
	static Logger logger = LoggerFactory.getLogger(Index.class);
	Map<String, Set<String>> map = new HashMap<String, Set<String>>();
	private String indexName = "index";

	public Index(String name) {
		this.indexName = name;
	}

	public void addEntry(String value, String customerNumber) {
		if (value == null || value.length() == 0) {
			logger.error("Null value in Index.addentry");
			return;
		}

		if (customerNumber == null || customerNumber.length() == 0) {
			logger.error("Null customerNumber in Index.addentry for "
					+ indexName);
			return;
		}

		// value=value.toLowerCase();
		if (!map.containsKey(value)) {
			map.put(value, new HashSet<String>());
		}

		Set<String> list = map.get(value);
		list.add(customerNumber);
	}

	public boolean load(Connection conn, String statement) {
		int count = 0;

		logger.trace("About to load index for " + indexName);
		logger.trace("Select statement: " + statement);
		Statement s = null;
		ResultSet rs = null;
		try {
			s = conn.createStatement();
			boolean result = s.execute(statement);
			if (result == false) {

				throw new Exception("could not execute select");
			}
			rs = s.getResultSet();
			if (rs == null) {
				logger.error("could not get result set");
				return false;
			}
			logger.trace("Got result set");
			while (rs.next()) {
				if (++count % 200000 == 0)
					logger.debug("Loading entry " + count);
				addEntry(rs.getString(1), rs.getString(2));
			}
			s.close();
		} catch (Exception e) {
			logger.error("Exception loading names: " + e.getMessage(), e);
			return false;
		} finally {
			close(rs, s, conn);
		}

		logger.trace("Loaded " + map.size() + " index entries");

		return true;
	}

	// TODO: Refactor this into a DBUtils class
	public static void close(ResultSet rs, Statement ps, Connection conn) {
		if (rs != null) {
			try {
				rs.close();

			} catch (SQLException e) {
				logger.error("The result set cannot be closed.", e);
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error("The statement cannot be closed.", e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("The data source connection cannot be closed.", e);
			}
		}

	}

	/**
	 * Search for customers having this identifier value
	 * 
	 * @param compareType
	 *            String e.g. "startsWith"
	 * @param value
	 * @param maxResults
	 * @return a List set of string with [customernumber] : [Name] for each
	 *         matching customer
	 * @throws EncoderException
	 */
	/*
	 * public List<String> search(int compareType, String value, int maxResults)
	 * throws EncoderException { List<String> results = new ArrayList<String>();
	 * Pattern p = null; Encoder dm = null; String dmcomp = null;
	 * 
	 * // logger.debug("Index: " + indexName + " is searching for " + value + //
	 * " with type " + compareType);
	 * 
	 * value = value.trim(); if (compareType == Config.COMPARE_REGEX) { p =
	 * Pattern.compile(value, Pattern.CASE_INSENSITIVE); } else if (compareType
	 * == Config.COMPARE_SOUNDSLIKE) { dm = new DoubleMetaphone(); dmcomp =
	 * (String) dm.encode((Object) value); }
	 * 
	 * for (String name : map.keySet()) { if (results.size() > maxResults)
	 * break; boolean matches = false; switch (compareType) { case
	 * Config.COMPARE_STARTSWITH: if
	 * (name.toLowerCase().startsWith(value.toLowerCase())) matches = true;
	 * break; case Config.COMPARE_CONTAINS: if
	 * (name.toLowerCase().contains(value.toLowerCase())) matches = true; break;
	 * case Config.COMPARE_EQUALS: if (name.equalsIgnoreCase(value)) matches =
	 * true; break; case Config.COMPARE_REGEX: Matcher m = p.matcher(name); if
	 * (m.matches()) matches = true; break; case Config.COMPARE_SOUNDSLIKE: for
	 * (String s : name.split("\b")) { if (dm.encode(s).equals(dmcomp)) {
	 * matches = true; logger.debug("Found " + s + " in " + name); } } break;
	 * default: logger.error("Invalid compare type " + compareType);
	 * 
	 * } // switch if (matches) { for (String customer : map.get(name)) { while
	 * (customer.length() < 8) customer = " " + customer; results.add(customer +
	 * ":" + name); } } } // for
	 * 
	 * logger.debug("Returning " + results.size() + " results"); return results;
	 * }
	 * 
	 * public String getIndexName() { return indexName; }
	 */
}
