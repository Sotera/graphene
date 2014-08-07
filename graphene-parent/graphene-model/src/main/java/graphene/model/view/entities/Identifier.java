package graphene.model.view.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Identifier implements Comparable<Identifier> {

	private String datasource_id;
	@Deprecated
	private String fulladdress = null;
	private String id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Identifier ["
				+ (datasource_id != null ? "datasource_id=" + datasource_id
						+ ", " : "")
				+ (fulladdress != null ? "fulladdress=" + fulladdress + ", "
						: "") + (id != null ? "id=" + id : "") + "]";
	}

	public Identifier() {
	}

	/**
	 * 
	 * @param source
	 *            String datasource ID
	 * @param id
	 *            String unique ID for the EmailAddress within the source
	 * @param address
	 *            String the address
	 */
	public Identifier(final String source, final String id, final String address) {
		this.datasource_id = source;
		this.id = id;
		this.fulladdress = address;
	}

	public String getDatasource_id() {
		return datasource_id;
	}

	public String getFullAddress() {
		return fulladdress;
	}

	public String getId() {
		return id;
	}

	public void setDatasource_id(String datasource_id) {
		this.datasource_id = datasource_id;
	}

//	public void setFullAddress(String address) {
//		this.fulladdress = address;
//	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		return this.fulladdress.equals(((Identifier) o).fulladdress);
	}

	@Override
	public int hashCode() {
		return this.fulladdress.hashCode();
	}

	@Override
	public int compareTo(Identifier o) {
		return this.id.compareTo(o.id);
	}
}
