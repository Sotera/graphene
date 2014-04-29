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

	public EntityAttribute(final String family, final String name, final String idValue) {
		this.family = family;
		this.name = name;
		this.value = idValue;
	}

	public EntityAttribute(final String family, final String idValue) {
		this.family = family;
		this.value = idValue;
	}

	public String getFamily() {
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

	@Override
	public boolean equals(final Object o) {
		if (o == null)
			return false;
		EntityAttribute a = (EntityAttribute) o;
		return a.getFamily().equals(family) && a.getName().equals(name)
				&& a.getValue().equals(value);
	}

	@Override
	public int hashCode() {
		return (family + name + value).hashCode();

	}
}
