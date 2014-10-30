package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

import java.util.List;
import java.util.regex.Pattern;

public class EmailExtractor  extends AbstractExtractor{
	private final static String RE_MAIL = "([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})";

	public EmailExtractor() {
		p = Pattern.compile(RE_MAIL);
	}

	@Override
	public String getIdType() {
		return "Potential Email Address";
	}

	@Override
	public String getNodetype() {
		return "Extracted"+ G_CanonicalPropertyType.EMAIL_ADDRESS.name();
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.COMMUNICATION_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential Email Address";
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}
}
