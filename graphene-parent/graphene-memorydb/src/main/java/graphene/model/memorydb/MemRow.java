package graphene.model.memorydb;

import java.util.Arrays;

/**
 * Used to encapsulate a row from the database table in compressed format, where
 * custno etc are offsets into an array of strings for the respective entity.
 * 
 * @author PWG for DARPA
 * 
 */

public class MemRow {

	public int offset; // used for de-dupe
	public int[] entries = { -1, -1, -1 };
	public int idType;
	public int[] nextrows = { -1, -1, -1 };

	public MemRow() {

	}

//	@Deprecated
//	public String getIdValue() {
//		return MemoryDB.getIdValueForID(entries[MemoryDB.IDENTIFIER]);
//	}

//	@Deprecated
//	public void setIdValue(String idValue) {
//		entries[MemoryDB.IDENTIFIER] = MemoryDB
//				.getIdentifierIDForValue(idValue);
//	}

//	@Deprecated
//	public String getCustomerNumber() {
//		return MemoryDB.getCustomerNumberForID(entries[MemoryDB.CUSTOMER]);
//	}

//	@Deprecated
//	public void setCustomerNumber(String customerNumber) {
//		entries[MemoryDB.CUSTOMER] = MemoryDB
//				.getCustomerIDForNumber(customerNumber);
//	}

//	@Deprecated
//	public String getAccountNumber() {
//		return MemoryDB.getAccountNumberForID(entries[MemoryDB.ACCOUNT]);
//	}

//	@Deprecated
//	public void setAccountNumber(String accountNumber) {
//		entries[MemoryDB.ACCOUNT] = MemoryDB
//				.getAccountIDForNumber(accountNumber);
//	}

	public int getIdType() {
		return idType;
	}

	public void setIdType(int type) {
		idType = type;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

//	@Deprecated
//	public void setCustno(int custno) {
//		entries[MemoryDB.CUSTOMER] = custno;
//	}
//
//	@Deprecated
//	public void setAcno(int acno) {
//		entries[MemoryDB.ACCOUNT] = acno;
//	}
//
//	@Deprecated
//	public void setidNo(int idno) {
//		entries[MemoryDB.IDENTIFIER] = idno;
//	}

//	@Deprecated
//	public int getCustno() {
//		return entries[MemoryDB.CUSTOMER];
//	}

//	@Deprecated
//	public int getAcno() {
//		return entries[MemoryDB.ACCOUNT];
//	}

//	@Deprecated
//	public int getIdno() {
//		return entries[MemoryDB.IDENTIFIER];
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + offset;
		return result;
	}

	@Override
	public String toString() {
		return "MemRow [offset="
				+ offset
				+ ", "
				+ (entries != null ? "entries=" + Arrays.toString(entries)
						+ ", " : "")
				+ "idType="
				+ idType
				+ ", "
				+ (nextrows != null ? "nextrows=" + Arrays.toString(nextrows)
						: "") + "]";
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
		MemRow other = (MemRow) obj;
		if (offset != other.offset)
			return false;
		return true;
	}

}