package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;

import java.util.regex.Pattern;

public class EmailExtractor extends AbstractExtractor {
	private final static String RE = "([\\w\\-]([\\.\\w])+[\\w]+(@|\\s+at\\s+|\\s*[\\[\\(]\\s*at\\s*[\\)\\]]\\s*)([\\w\\-]+(\\.|\\s*[\\[\\(]\\s*(\\.|dot)\\s*[\\)\\]]\\s*))+[A-Za-z]{2,4})";

	public EmailExtractor() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + RE);
		p = Pattern.compile(RE);
	}

	@Override
	public String getIdType() {
		return "Potential Email Address";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.EMAIL_ADDRESS.name();
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
	public String postProcessMatch(final String match) {
		return match.replaceAll("\\s+at\\s+|\\s*[\\[\\(]\\s*at\\s*[\\)\\]]\\s*", "@").replaceAll(
				"\\s*[\\[\\(]\\s*(\\.|dot)\\s*[\\)\\]]\\s*", ".");
	}
}
