package graphene.hts.keywords;

import java.util.List;
import java.util.regex.Pattern;

import graphene.hts.entityextraction.AbstractExtractor;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

public class KeywordExtractorImpl extends AbstractExtractor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.hts.keywords.KeywordExtractor#getSimpleKeywords(java.lang.String
	 * )
	 */
	private final static String RE_1 = "([\\w\\d]{3,})";

//	@Override
//	public SortedSet<String> getSimpleKeywords(String narrative) {
//		TreeSet<String> keyWords = new TreeSet<String>(
//				Arrays.asList(((String) narrative).split("([\\w\\d]{3,})")));
//		for (Iterator<String> iter = keyWords.iterator(); iter.hasNext();) {
//			String k = iter.next();
//			if (k.length() < 2) {
//				iter.remove();
//			}
//		}
//		return keyWords;
//	}

	public KeywordExtractorImpl() {
		p = Pattern.compile(RE_1);
	}
	@Override
	public String getIdType() {
		return "Keyword";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.ANY.name();
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.IN_DOCUMENT.name();
	}

	@Override
	public String getRelationValue() {
		return "Keyword in document";
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
