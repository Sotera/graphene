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

	public int getIdType() {
		return idType;
	}

	public void setIdType(int type) {
		idType = type;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

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