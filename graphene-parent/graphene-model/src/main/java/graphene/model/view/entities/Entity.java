package graphene.model.view.entities;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import mil.darpa.vande.generic.V_IdProperty;

/**
 * 
 * This is a more generic replacement for the Customer
 * object. It depends on an Entity DAO which is more of a virtual Entity
 * DAO because the Entity might not correspond to a single table.
 * <BR/>The list of attributes uses lazy loading. It only retrieves them when
 * requested, and until then the list is null. The DAO should return an
 * empty list if none found
 * @author PWG for DARPA
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Entity {
	private Set<Account> accountList = null;
	private Set<Identifier> addressList = null;
	private String datasourceId = null;
	private Set<EmailAddress> emailList = null;
	// as can have multiple values for a
	// key
	private boolean fullyLoaded = false;
	private String id = null;
	public Set<V_IdProperty> identList = null;// cannot use java.util.Properties
	private Set<Name> nameList = null;
	private Set<CommunicationId> communicationIds = null;

	public Entity() {

	}

	/**
	 * Constructor
	 * 
	 * @param source
	 *            String datasource ID
	 * @param id
	 *            String unique ID for the entity within the source
	 */
	public Entity(final String source, final String id) {
		this.datasourceId = source;
		this.id = id;
	}

	public void addAddress(final Identifier e) {
		if (addressList == null)
			addressList = new HashSet<Identifier>();
		addressList.add(e);
	}

	public void addEmailAddress(final EmailAddress e) {
		if (emailList == null)
			emailList = new HashSet<EmailAddress>();
		emailList.add(e);
	}

	public void addName(final Name e) {
		if (nameList == null)
			nameList = new HashSet<Name>();
		nameList.add(e);
	}

	public void addCommunicationId(final CommunicationId communicationId) {
		if (communicationIds == null)
			communicationIds = new HashSet<CommunicationId>();
		communicationIds.add(communicationId);
	}
	public void addAccount(final Account e) {
		if (accountList == null)
			accountList = new HashSet<Account>();
		accountList.add(e);
	}

	public Set<Account> getAccountList() {
		return accountList;
	}

	public Set<Identifier> getAddressList() {
		return addressList;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public Set<EmailAddress> getEmailList() {
		return emailList;
	}

	public String getId() {
		return id;
	}

	public Set<V_IdProperty> getIdentList() {
		return identList;
	}

	public Set<Name> getNameList() {
		return nameList;
	}

	public Set<CommunicationId> getCommunicationIdList() {
		return communicationIds;
	}

	public boolean isFullyLoaded() {
		return fullyLoaded;
	}

	public void setAccountList(final Set<Account> set) {
		this.accountList = set;
	}

	public void setAddressList(final Set<Identifier> addressList) {
		this.addressList = addressList;
	}

	public void setDatasourceId(final String id) {
		this.datasourceId = id;
	}

	public void setEmailList(final Set<EmailAddress> emailList) {
		this.emailList = emailList;
	}

	public void setFullyLoaded(final boolean fullyLoaded) {
		this.fullyLoaded = fullyLoaded;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setIdentList(final Set<V_IdProperty> identList) {
		this.identList = identList;
	}

	public void setNameList(final Set<Name> nameList) {
		this.nameList = nameList;
	}

	public void setCommunicationIdList(final Set<CommunicationId> communicationIds) {
		this.communicationIds = communicationIds;
	}

}
