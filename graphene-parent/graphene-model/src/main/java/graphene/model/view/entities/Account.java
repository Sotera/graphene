package graphene.model.view.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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

	public Account() {
	}

	public Account(String datasource, String id, String accountNumber) {
		this.setDatasource_id(datasource);
		this.id = id;
		this.accountNumber = accountNumber;
	}

	@Override
	public boolean equals(Object o) {
		return accountNumber.equals(((Account) o).getAccountNumber());
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

	@Override
	public int hashCode() {
		return accountNumber.hashCode();
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

}
