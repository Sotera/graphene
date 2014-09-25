package graphene.model.view.entities;

import graphene.model.idl.G_Property;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 
 * This is a more generic replacement for the Customer object. It depends on an
 * Entity DAO which is more of a virtual Entity DAO because the Entity might not
 * correspond to a single table. <BR/>
 * The list of attributes uses lazy loading. It only retrieves them when
 * requested, and until then the list is null. The DAO should return an empty
 * list if none found
 * 
 * @author PWG for DARPA
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Entity {
	private Set<G_Property> accountList = null;
	private Set<G_Property> addressList = null;
	private Set<G_Property> communicationIds = null;
	private String datasourceId = null;
	private Set<G_Property> emailList = null;
	// as can have multiple values for a
	// key
	private boolean fullyLoaded = false;
	private String id = null;
	private Set<G_Property> identList = null;

	private Set<G_Property> nameList = null;
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

	public void addAccount(final G_Property e) {
		if (accountList == null) {
			accountList = new HashSet<G_Property>(2);
		}
		accountList.add(e);
	}

	public void addAddress(final G_Property e) {
		if (addressList == null) {
			addressList = new HashSet<G_Property>(2);
		}
		addressList.add(e);
	}

	public void addCommunicationId(final G_Property p) {
		if (communicationIds == null) {
			communicationIds = new HashSet<G_Property>(2);
		}
		communicationIds.add(p);
	}

	public void addEmailAddress(final G_Property e) {
		if (emailList == null) {
			emailList = new HashSet<G_Property>(2);
		}
		emailList.add(e);
	}

	public void addIdentifier(final G_Property e) {
		if (identList == null) {
			identList = new HashSet<G_Property>(2);
		}
		identList.add(e);
	}

	public void addName(final G_Property e) {
		if (nameList == null) {
			nameList = new HashSet<G_Property>(2);
		}
		nameList.add(e);
	}

	public Set<G_Property> getAccountList() {
		return accountList;
	}

	public Set<G_Property> getAddressList() {
		return addressList;
	}

	/**
	 * @return the communicationIds
	 */
	public final Set<G_Property> getCommunicationIds() {
		return communicationIds;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public Set<G_Property> getEmailList() {
		return emailList;
	}

	public String getId() {
		return id;
	}

	public Set<G_Property> getIdentList() {
		return identList;
	}

	public Set<G_Property> getNameList() {
		return nameList;
	}

	public boolean isFullyLoaded() {
		return fullyLoaded;
	}

	public void setAccountList(final Set<G_Property> set) {
		this.accountList = set;
	}

	public void setAddressList(final Set<G_Property> addressList) {
		this.addressList = addressList;
	}

	/**
	 * @param communicationIds
	 *            the communicationIds to set
	 */
	public final void setCommunicationIds(Set<G_Property> communicationIds) {
		this.communicationIds = communicationIds;
	}

	public void setDatasourceId(final String id) {
		this.datasourceId = id;
	}

	public void setEmailList(final Set<G_Property> emailList) {
		this.emailList = emailList;
	}

	public void setFullyLoaded(final boolean fullyLoaded) {
		this.fullyLoaded = fullyLoaded;
	}

	public void setG_PropertyList(final Set<G_Property> G_PropertyList) {
		this.nameList = G_PropertyList;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setIdentList(final Set<G_Property> identList) {
		this.identList = identList;
	}

	/**
	 * @param nameList the nameList to set
	 */
	public final void setNameList(Set<G_Property> nameList) {
		this.nameList = nameList;
	}

}
