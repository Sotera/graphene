package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalRelationshipType;

import java.util.regex.Pattern;

public class ContainsGoogleSearch extends AbstractExtractor {
	private final static String RE_PATTERN = "Google Search";

	public ContainsGoogleSearch() {
		p = Pattern.compile(RE_PATTERN);
	}

	@Override
	public String getIdType() {
		return "Contains Phrase";
	}

	@Override
	public String getNodetype() {
		return "Extracted Phrase";
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.CONTAINED_IN.name();
	}

	@Override
	public String getRelationValue() {
		return "Contains Phrase";
	}

	@Override
	public String postProcessMatch(final String match) {
		return match;
	}
}
