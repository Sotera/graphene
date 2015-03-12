package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;

import java.util.regex.Pattern;

public class URLExtractor extends AbstractExtractor {
	private final static String RE = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

	public URLExtractor() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + RE);
		p = Pattern.compile(RE);
	}

	@Override
	public String getIdType() {
		return "Potential URL";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.URL.name();
	}

	@Override
	public String getRelationType() {
		return "Website";
	}

	@Override
	public String getRelationValue() {
		return "Potential Website";
	}
}
