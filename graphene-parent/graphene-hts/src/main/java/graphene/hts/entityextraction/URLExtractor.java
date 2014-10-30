package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLExtractor  extends AbstractExtractor {
	private final static String RE_URL = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	
	public URLExtractor() {
		p = Pattern.compile(RE_URL);
	}

	@Override
	public String getIdType() {
		return "Potential URL";
	}

	@Override
	public String getNodetype() {
		return "Extracted"+ G_CanonicalPropertyType.URL.name();
	}

	@Override
	public String getRelationType() {
		return "Website";
	}

	@Override
	public String getRelationValue() {
		return "Potential Website";
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
