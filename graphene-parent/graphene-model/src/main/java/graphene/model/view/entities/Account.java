package graphene.model.view.entities;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * This class can be used for any type of company record where an entity has
 * registered and is assigned one or more associated identifiers (usually
 * numeric), for example Kiva or a forum, etc.
 * 
 * XXX: This is probably too specific, we have more generic types that can be
 * used instead, with less custom code.--djue
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Account {
	private String accountNumber;
	private String datasource_id = null;
	private String id; // can be the same as accountNumber dep on platform
	@XmlTransient
	@JsonIgnore
	private Entity owner = null;
	@JsonIgnore
	private String ownerId; // can use for navigation
	@JsonIgnore
	private Properties properties = null;

	public Account() {
	}

	public Account(String datasource, String id, String accountNumber) {
		this.setDatasource_id(datasource);
		this.id = id;
		this.accountNumber = accountNumber;
	}

	/**
	 * Can use for adding properties such as average balance etc.
	 * 
	 * @param name
	 * @param value
	 */
	public void addProperty(String name, String value) {
		if (properties == null)
			properties = new Properties();
		properties.put(name, value);
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getDatasource_id() {
		return datasource_id;
	}

	public String getId() {
		return id;
	}

	@JsonIgnore
	@XmlTransient
	public Entity getOwner() {
		return owner;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setDatasource_id(String datasource_id) {
		this.datasource_id = datasource_id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	@XmlTransient
	public void setOwner(Entity owner) {
		this.owner = owner;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public boolean equals(Object o) {
		return accountNumber.equals(((Account) o).getAccountNumber());
	}

	@Override
	public int hashCode() {
		return accountNumber.hashCode();
	}

}
