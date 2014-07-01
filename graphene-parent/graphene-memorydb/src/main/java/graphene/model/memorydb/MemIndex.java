package graphene.model.memorydb;

import graphene.util.validator.ValidationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemIndex {

	private static final boolean DEBUG = false;
	private String[] values;
	String name = "new";
	private int[] heads; // the first row in the grid for each value
	private int[] tails; // the last row in the grid for each value

	private static Logger logger = LoggerFactory.getLogger(MemIndex.class);

	public MemIndex(String name) {
		this.name = name; // to help in debugging
	}

	public void load(String[] vals) {
		// cloning is the thread safe way, not storing a reference to a mutable
		Arrays.sort(vals); // Even though we did a SELECT .. order by, we still
							// need to sort it. The reason is that
							// SQLServer order by is case-insensitive, which
							// breaks binarySearch which is
							// case sensitive
		setValues(vals.clone());
		setHeads(new int[vals.length]);
		for (int i = 0; i < vals.length; ++i)
			getHeads()[i] = -1;
		setTails(new int[vals.length]);
		for (int i = 0; i < vals.length; ++i)
			getTails()[i] = -1;

	}

	public String getValueForID(int id) {
		if (id == -1)
			return null; // TODO: or "" ?
		if (id > values.length) {
			logger.warn("Index id " + id + " out of range:" + values.length);
			return null;
		}
		return values[id];
	}

	/**
	 * 
	 * @param value
	 *            an identifierString, a customerNumber or an accountNumber.
	 * @return
	 */
	public int getIDForValue(String value) {
		if (!ValidationUtils.isValid(value)) {
			return -1;
		}
		int result = Arrays.binarySearch(getValues(), value);
		if (result < 0) {
			logger.debug("Index " + name + " could not find Id for " + value
					+ " using binary search");
			if (DEBUG) {
				// Try direct search - just for debugging
				for (String s : getValues()) {
					if (s.equals(value)) {
						logger.error("Bsearch for " + value
								+ " failed but found by array search");
						break;
					}
					if (s.equalsIgnoreCase(value)) {
						logger.error("Bsearch for "
								+ value
								+ " failed but found by array search using ignore case");
						break;
					}
				}
			}
		}
		return result;
		// return bSearch(values, value);
	}

	public int getCount() {
		return getValues().length;
	}

	static int bSearch(String[] values, String target) {
		int low = 0;
		int high = values.length - 1;
		int mid = 0;

		while (low <= high) {
			mid = low + ((high - low) / 2);
			int result = target.compareTo(values[mid]);
			if (result < 0)
				high = mid - 1;
			else if (result > 0)
				low = mid + 1;
			else
				return mid;
		}

		return -1;

	}

	public Set<String> findValuesContaining(Set<String> vals,
			boolean caseSensitive) {
		Set<String> results = new HashSet<String>();
		String candidate;
		for (String s : getValues()) {
			candidate = caseSensitive ? s : s.toLowerCase();
			boolean found = true;
			for (String q : vals) {
				if (caseSensitive == false)
					q = q.toLowerCase();
				if (!candidate.contains(q))
					found = false;
			}
			if (found)
				results.add(s);
		}
		return results;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public int[] getTails() {
		return tails;
	}

	public int[] setTails(int[] tails) {
		this.tails = tails;
		return tails;
	}

	public int[] getHeads() {
		return heads;
	}

	public void setHeads(int[] heads) {
		this.heads = heads;
	}

}
