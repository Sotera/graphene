package graphene.model.view.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdType {

	static Logger logger = LoggerFactory.getLogger(IdType.class);

	private String columnSource;

	private String family;
	
	private int idtype_id;

	private String shortName;

	private String tableSource;

//	private G_CanonicalPropertyType type;

	public IdType() {
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdType other = (IdType) obj;
		if (columnSource == null) {
			if (other.columnSource != null)
				return false;
		} else if (!columnSource.equals(other.columnSource))
			return false;
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
			return false;
		if (idtype_id != other.idtype_id)
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (tableSource == null) {
			if (other.tableSource != null)
				return false;
		} else if (!tableSource.equals(other.tableSource))
			return false;
//		if (type != other.type)
//			return false;
		return true;
	}

	public String getColumnSource() {
		return columnSource;
	}


	public String getNodeType() {
		return family;
	}

	public int getIdType_id() {
		return idtype_id;
	}

	public String getShortName() {
		return shortName;
	}

	public String getTableSource() {
		return tableSource;
	}

//	public G_CanonicalPropertyType getType() {
//		return type;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnSource == null) ? 0 : columnSource.hashCode());
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + idtype_id;
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result
				+ ((tableSource == null) ? 0 : tableSource.hashCode());
		//result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public void setColumnSource(final String columnSource) {
		this.columnSource = columnSource;
	}

	public void setFamily(final String family) {
		this.family = family;
	}

	public void setIdType_id(final int idtype_id) {
		this.idtype_id = idtype_id;
	}

	public void setShortName(final String shortName) {
		this.shortName = shortName;
	}

	public void setTableSource(final String tableSource) {
		this.tableSource = tableSource;
	}

//	public void setType(final G_CanonicalPropertyType type) {
//		this.type = type;
//	}

//	@Override
//	public String toString() {
//		return "IdType [columnSource=" + columnSource + ", family=" + family
//				+ ", type=" + type + ", idtype_id=" + idtype_id
//				+ ", shortName=" + shortName + ", tableSource=" + tableSource
//				+ "]";
//	}

}
