package graphene.model.view.entities;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Version of Entity that contains lists of EntityAttributes - i.e. it contains
 * lists of String properties rather than typed objects
 * 
 * @author PWG for DARPA
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityLight {
	private String id = null;
	private Set<EntityAttribute> attributes = new HashSet<EntityAttribute>();
	private Set<String> accountList = new HashSet<String>();
	private String datasource_id = null;
	private String effectiveName = null;
	private String allNames = null;


	public EntityLight() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the allNames
	 */
	public final String getAllNames() {
		return allNames;
	}

	/**
	 * @param allNames the allNames to set
	 */
	public final void setAllNames(String allNames) {
		this.allNames = allNames;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public final void setAttributes(Set<EntityAttribute> attributes) {
		this.attributes = attributes;
	}

	public String getEffectiveName() {
		return effectiveName;
	}

	public void setEffectiveName(final String effectiveName) {
		this.effectiveName = effectiveName;
	}

	public String getDatasource_id() {
		return datasource_id;
	}

	public void setDatasource_id(final String datasource_id) {
		this.datasource_id = datasource_id;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Set<String> getAccountList() {
		return accountList;
	}

	public void setAccountList(final Set<String> accountList) {
		this.accountList = accountList;
	}

	public Set<EntityAttribute> getAttributes() {
		return attributes;
	}
}
