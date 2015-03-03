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
	private final static String RE_USSSN = "\\b((?!000)([0-6]\\d{2}|7([0-6]\\d|7[012]))([ -]?)(?!00)\\d\\d\\3(?!0000)\\d{4})\\b";
	/*
	 * http://stackoverflow.com/questions/4087468/ssn-regex-for-123-45-6789-or-xxx
	 * -xx-xxxx
	 */
	private final static String FORBID_PREFIX = "((?<!(\\d)|(accounts?\\s\\#\\s?)|(Code\\s)|(\\#\\s?))";
	private final static String FORBID_SUFFIX = "(?![\\w]))";

	private final static String RE_USSSN2 = "((?!(000|666|9))\\d{3}([ -]?)(?!00)\\d{2}([ -]?)(?!0000)\\d{4})";

	/**
	 * This works but is too simplistic
	 */
	private final static String RE_USSSN3 = "\\b(\\d{3}[- ]?\\d{2}[- ]?\\d{4})\\b";
	private final static String REQUIRE_PREFIX = "((?<=(EIN|TIN|SSN)\\s{0,5})";

	public USSSNExtractor() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + REQUIRE_PREFIX + RE_USSSN2
				+ FORBID_SUFFIX);
		p = Pattern.compile(REQUIRE_PREFIX + RE_USSSN2 + FORBID_SUFFIX);
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdType() {
		return "Potential US SSN";
	}

	@Override
	public String getNodetype() {
		return "Extracted " + G_CanonicalPropertyType.GOVERNMENTID.name();
	}

	@Override
	public List<G_Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRelationType() {
		return "Extracted " + G_CanonicalRelationshipType.GOVERNMENT_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential US SSN";
	}

	@Override
	public String postProcessMatch(final String match) {
		// replace spaces or dashes with nothing
		return match.replaceAll("\\s|-", "");
	}
}
