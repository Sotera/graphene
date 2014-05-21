package graphene.model.view.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Note that some platforms have a EmailAddress table, which can be used to
 * traverse across entities sharing an address by using the unique ID
 * 
 * @author PWG for DARPA
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Identifier {

	private String datasource_id;
	private String fulladdress = null;
	private String id;


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

	public void setFullAddress(String address) {
		this.fulladdress = address;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Override
	public boolean equals(Object o)
	{
		return this.fulladdress.equals(((Identifier) o).fulladdress);
	}
	@Override
	public int hashCode()
	{
		return this.fulladdress.hashCode();
	}
}
