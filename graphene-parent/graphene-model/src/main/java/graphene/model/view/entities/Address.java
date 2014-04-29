package graphene.model.view.entities;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Note that some platforms have a EmailAddress table, which can be used to
 * traverse across entities sharing an address by using the unique ID
 * 
 * @author PWG for DARPA
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Address {

	private String datasource_id;
	private String fulladdress = null;
	private String id;
	// Following transients are for future use
	@JsonIgnore
	private Properties properties;

	public Address() {
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
	public Address(final String source, final String id, final String address) {
		this.datasource_id = source;
		this.id = id;
		this.fulladdress = address;
	}

	/**
	 * @param name
	 * @param value
	 */
	public void addProperty(final String name, final String value) {
		properties.put(name, value);
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

	public Properties getProperties() {
		return properties;
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
		return this.fulladdress.equals(((Address) o).fulladdress);
	}
	@Override
	public int hashCode()
	{
		return this.fulladdress.hashCode();
	}
}
