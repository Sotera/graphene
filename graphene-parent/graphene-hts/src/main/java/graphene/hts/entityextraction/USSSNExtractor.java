package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

import java.util.List;
import java.util.regex.Pattern;

/**
 * This regex validates U.S. social security numbers, within the range of
 * numbers that have been currently allocated.
 * 
 * @author djue
 * 
 */
public class USSSNExtractor extends AbstractExtractor {
	/**
	 * http://www.regxlib.com/DisplayPatterns.aspx?cattabindex=3&categoryId=4
	 */
	private final static String RE_USSSN = "((?!000)([0-6]\\d{2}|7([0-6]\\d|7[012]))([ -]?)(?!00)\\d\\d\\3(?!0000)\\d{4})";
	/*
	 * http://stackoverflow.com/questions/4087468/ssn-regex-for-123-45-6789-or-xxx
	 * -xx-xxxx
	 */
	private final static String RE_USSSN2 = "((?!(000|666|9))\\d{3}-(?!00)\\d{2}-(?!0000)\\d{4})";

	public USSSNExtractor() {
		p = Pattern.compile(RE_USSSN2);
	}

	@Override
	public String getIdType() {
		return "Potential US SSN";
	}

	@Override
	public String getNodetype() {
		return "Extracted "+G_CanonicalPropertyType.GOVERNMENTID.name();
	}

	@Override
	public String getRelationType() {
		return "Extracted "+G_CanonicalRelationshipType.GOVERNMENT_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential US SSN";
	}

	@Override
	public String postProcessMatch(String match) {
		// replace spaces or dashes with nothing
		return match.replaceAll("\\s|-", "");
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
