package graphene.model.memorydb;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This seems to be unused. --djue
 * 
 * @author pwg
 * 
 */
public class IndexEntry {
	public String value;
	public int typeCode = 0;
	public List<String> customerList = new ArrayList<String>();

	static Logger logger = LoggerFactory.getLogger(IndexEntry.class);

	public IndexEntry(String value, boolean isPhone, boolean isName,
			boolean isAddress) {
		if (value == null) {
			logger.trace("Null value on IndexEntry constructor");
		}
		this.value = value;
		typeCode = makeTypeCode(isPhone, isName, isAddress);
	}

	public void addCustomer(String c) {
		customerList.add(c);
	}

	/*
	 * Bug: equals(Object) does not check
	 * for null argument
	 * 
	 * This implementation of equals(Object) violates the contract defined by
	 * java.lang.Object.equals() because it does not check for null being passed
	 * as the argument. All equals() methods should return false if passed a
	 * null value.
	 * 
	 * 
	 * @Override public boolean equals(Object o) { IndexEntry target =
	 * (IndexEntry) o; return ((typeCode == target.typeCode) &&
	 * (target.value.equals(value))); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + typeCode;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexEntry other = (IndexEntry) obj;
		if (typeCode != other.typeCode)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public boolean getIsPhone() {
		return (typeCode & 1) == 1;
	}

	public boolean getIsName() {
		return (typeCode & 2) == 2;
	}

	public boolean getIsAddress() {
		return (typeCode & 4) == 4;
	}

	public static int makeTypeCode(boolean isPhone, boolean isName,
			boolean isAddress) {
		int type = 0;

		if (isPhone)
			type |= 1;
		if (isName)
			type |= 2;
		if (isAddress)
			type |= 4;

		return type;
	}

}
