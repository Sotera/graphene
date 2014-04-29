package graphene.model.datasourcedescriptors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A field within a DataSet, which in turn is a real or virtual table within a
 * DataSource.
 * 
 * @author pgofton
 * 
 *         My interpretation of some seemingly unused fields: Searchable means
 *         we can search on it (verus just being able to display it) Reportable
 *         means we can display it. Sortable means we can rank on it.
 * 
 *         This leads to some interesting cases:
 * 
 *         We might be able to search on a field, but not display the contents
 *         of the field.
 * 
 *         We might also be able to search on a field and sort by it, but still
 *         not see the contents.
 * 
 *         We might be able to report on it, and sort by it, but not search on
 *         it.
 * 
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DataSetField {
	public String name;
	public String friendlyName;
	public String type; // string, date, unit, number, boolean
	public boolean sortable;
	public boolean searchable;
	public boolean reportable;

	// TODO perhaps add private table and column names

	public DataSetField() {

	}

	/**
	 * 
	 * @param name
	 *            internal name
	 * @param friendlyName
	 *            friendly name
	 * @param type
	 *            string, date etc
	 * @param sortable
	 *            boolean
	 * @param searchable
	 *            boolean
	 * @param reportable
	 *            boolean
	 */
	public DataSetField(String name, String friendlyName, String type,
			boolean sortable, boolean searchable, boolean reportable) {
		this.name = name;
		this.friendlyName = friendlyName;
		this.type = type;
		this.sortable = sortable;
		this.searchable = searchable;
		this.reportable = reportable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public boolean isReportable() {
		return reportable;
	}

	public void setReportable(boolean reportable) {
		this.reportable = reportable;
	}

}
