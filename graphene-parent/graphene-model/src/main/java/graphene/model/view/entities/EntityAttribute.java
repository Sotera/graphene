package graphene.model.view.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Encapsulates one attribute with its family. For example Phone, Mobile Phone,
 * 123-1234 <BR/>
 * Families are used (a) so that we can discover links based on any member of
 * the family, and (b) so we can show appropriate icons.
 * 
 * @author PWG for DARPA
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityAttribute {
	public String family = ""; // default value so equals doesn't fail
	public String name = "";
	public String value = "";

	public EntityAttribute() {

	}

	public EntityAttribute(final String family, final String name,
			final String idValue) {
		this.family = family;
		this.name = name;
		this.value = idValue;
	}

	public EntityAttribute(final String family, final String idValue) {
		this.family = family;
		this.value = idValue;
	}

	public String getNodeType() {
		return family;
	}

	public void setFamily(final String idFamily) {
		this.family = idFamily;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(final String idName) {
		this.name = idName;
	}

	public void setValue(final String idValue) {
		this.value = idValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EntityAttribute other = (EntityAttribute) obj;
		if (family == null) {
			if (other.family != null) {
				return false;
			}
		} else if (!family.equals(other.family)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
}
