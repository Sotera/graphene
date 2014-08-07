package graphene.model.view.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Note that some platforms have a EmailAddress table, which can be used to
 * traverse across entities sharing a EmailAddress address by using the unique
 * ID
 * 
 * @author PWG for DARPA
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailAddress {

	private String datasource_id;
	@Deprecated
	private String fulladdress = null;
	private String id;

	public EmailAddress() {
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
	public EmailAddress(final String source, final String id, final String address) {
		this.datasource_id = source;
		this.id = id;
		this.fulladdress = address;
	}


	@Override
	public boolean equals(final Object o)
	{
		return this.fulladdress.equals(((EmailAddress) o).fulladdress);
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


	@Override
	public int hashCode()
	{
		return this.fulladdress.hashCode();
	}

	public void setDatasource_id(final String datasource_id) {
		this.datasource_id = datasource_id;
	}
	public void setFullAddress(final String address) {
		this.fulladdress = address;
	}
	public void setId(final String id) {
		this.id = id;
	}
}
