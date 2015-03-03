package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

import java.util.List;
import java.util.regex.Pattern;

public class EmailToName extends AbstractExtractor {

	private final static String RE = "([\\w\\-]([\\.\\w])+[\\w]+(@|\\s+at\\s+|\\s*[\\[\\(]\\s*at\\s*[\\)\\]]\\s*)([\\w\\-]+(\\.|\\s*[\\[\\(]\\s*(\\.|dot)\\s*[\\)\\]]\\s*))+[A-Za-z]{2,4})";

	public EmailToName() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + RE);
		p = Pattern.compile(RE);
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdType() {
		return "Potential Alias";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.USERNAME.name();
	}

	@Override
	public List<G_Property> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRelationType() {
		return "Potential Alias";
	}

	@Override
	public String getRelationValue() {
		return "Potential Alias from Email Address";
	}

	private String nameFix(final String name) {
		if ((name == null) || (name.length() == 0)) {
			return name;
		} else {
			return ("" + name.charAt(0)).toUpperCase() + name.substring(1);
		}
	}

	@Override
	public String postProcessMatch(String match) {
		match = match.replaceAll("\\s+at\\s+|\\s*[\\[\\(]\\s*at\\s*[\\)\\]]\\s*", "@").replaceAll(
				"\\s*[\\[\\(]\\s*(\\.|dot)\\s*[\\)\\]]\\s*", ".");
		final int atpos = match.indexOf("@");
		if (atpos == -1) {
			return null;
		}
		String name = match.substring(0, atpos);

		final int dotpos = name.indexOf(".");
		if (dotpos != -1) {
			try {
				final String first = name.substring(0, dotpos);
				final String last = name.substring(dotpos + 1);
				name = nameFix(first) + " " + nameFix(last);
				name = name.trim();
			} catch (final Exception e) {
				System.out.println("Error fixing name " + name);
				return null;
			}
		}
		return name;
	}
}
