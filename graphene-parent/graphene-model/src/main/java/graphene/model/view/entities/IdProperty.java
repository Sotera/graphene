package graphene.model.view.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class IdProperty {
	public String idName;
	public String idValue;

	public IdProperty() {

	}

	public IdProperty(final String name, final String idValue) {
		this.idName = name;
		this.idValue = idValue;
	}

	public String getIdName() {
		return idName;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdName(final String idName) {
		this.idName = idName;
	}

	public void setIdValue(final String idValue) {
		this.idValue = idValue;
	}
}
