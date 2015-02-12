package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idlhelper.PropertyHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AccountExtractor extends AbstractExtractor {

	private final static String RE = "([a-z]{0,2}\\d{12,})";

	public AccountExtractor() {
		p = Pattern.compile(RE);
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		final List<G_EntityTag> tags = new ArrayList<G_EntityTag>();
		tags.add(G_EntityTag.ACCOUNT);
		return tags;
	}

	@Override
	public String getIdType() {
		return "Potential Account";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.ACCOUNT.name();
	}

	@Override
	public List<G_Property> getProperties() {
		final List<G_Property> tags = new ArrayList<G_Property>();
		tags.add(new PropertyHelper(G_PropertyTag.TYPE, getNodetype()));
		return tags;
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.COMMERCIAL_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential Account";
	}

}
