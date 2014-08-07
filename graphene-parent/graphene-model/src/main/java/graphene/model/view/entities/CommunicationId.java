package graphene.model.view.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Note that some datasets might have a CommunicationId table, which can be used
 * to traverse across entities sharing a communication id by using the unique ID
 * 
 * This is a generic example that can be used for any kind of semi-unique
 * identifier where the events between the ids is usually dealing with
 * information of some kind.
 * 
 * TODO: This is a duplicate of a more generic IDL class, replace this one.
 */
@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
public class CommunicationId {

	private String datasourceId;
	private String value = null;
	// Following transients are for future use
	@XmlTransient
	private String uid; // can be the same as value dep on platform

	public CommunicationId() {
	}

	/**
	 * 
	 * @param datasourceId
	 *            String datasource ID
	 * @param uid
	 *            String unique ID for the communication id within the source
	 * @param value
	 *            String the communication id
	 */
	public CommunicationId(final String datasourceId, final String uid,
			final String value) {
		this.datasourceId = datasourceId;
		this.uid = uid;
		this.value = value;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datasourceId == null) ? 0 : datasourceId.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommunicationId other = (CommunicationId) obj;
		if (datasourceId == null) {
			if (other.datasourceId != null)
				return false;
		} else if (!datasourceId.equals(other.datasourceId))
			return false;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
