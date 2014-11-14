package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idlhelper.PropertyHelper;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class AccountExtractor extends AbstractExtractor {

	private final static String RE = "([a-z]{0,2}\\d{12,})";

	public AccountExtractor() {
		p = Pattern.compile(RE);
	}

	@Override
	public String getIdType() {
		return "Potential Account";
	}

	public List<G_EntityTag> getEntityTags() {
		List<G_EntityTag> tags = Collections.EMPTY_LIST;
		tags.add(G_EntityTag.ACCOUNT);
		return tags;
	}

	public List<G_Property> getProperties() {
		List<G_Property> tags = Collections.EMPTY_LIST;
		tags.add(new PropertyHelper(G_PropertyTag.TYPE, getNodetype()));
		return tags;
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.ACCOUNT.name();
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
