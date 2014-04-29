package graphene.model.query;

import graphene.model.idl.G_SearchType;

public class EntitySearchOption {
	G_SearchType type;
	String value;

	public G_SearchType getType() {
		return type;
	}

	public void setType(G_SearchType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "EntitySearchOption [type=" + type + ", value=" + value + "]";
	}
}
