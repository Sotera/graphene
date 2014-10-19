package graphene.hts.sentences;

import graphene.hts.entityextraction.AbstractExtractor;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class SentenceExtractorImpl extends AbstractExtractor {

	private final static String RE_1 = "([\\w\\d]{3,})";

	public SentenceExtractorImpl() {
		p = Pattern.compile(RE_1);
	}

	@Override
	public Collection<String> extract(String source) {
		List<String> matchList = new ArrayList<String>();
		source = StringUtils.cleanUpAllCaps(source, 0.2d);
		matchList.addAll(StringUtils.convertToSentences(source));
		return matchList;
	}

	@Override
	public String getIdType() {
		return "Sentence";
	}

	@Override
	public String getNodetype() {
		return "Extracted Sentence";
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.IN_DOCUMENT.name();
	}

	@Override
	public String getRelationValue() {
		return "Contains Sentence";
	}
}
