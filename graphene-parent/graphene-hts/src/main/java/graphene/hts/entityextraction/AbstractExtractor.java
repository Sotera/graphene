package graphene.hts.entityextraction;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_Uncertainty;
import graphene.model.idlhelper.EntityHelper;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author djue
 * 
 */
public abstract class AbstractExtractor implements Extractor {

	private Matcher m;
	protected Pattern p;

	public Collection<String> extract(String source) {
		Set<String> matchList = new HashSet<String>();
		if (ValidationUtils.isValid(source)) {
			m = p.matcher(source);
			while (m.find()) {

				matchList.add(postProcessMatch(m.group(1)));

			}
		}
		return matchList;
	}

	/**
	 * Adds the abilty to post process the match so that it more closely
	 * resembles structured entities. Override in your concrete class if needed.
	 * 
	 * @param group
	 * @return
	 */
	public String postProcessMatch(String match) {
		return match;
	}

	@Override
	public Collection<G_Entity> extractEntities(String source) {
		Set<String> matchList = new HashSet<String>();
		if (ValidationUtils.isValid(source)) {
			m = p.matcher(source);
			while (m.find()) {
				matchList.add(postProcessMatch(m.group(1)));
			}
		}
		ArrayList<G_Entity> list = new ArrayList<G_Entity>();
		for (String id : matchList) {

			G_Provenance prov = new G_Provenance("Narrative");
			G_Uncertainty uncertainty = new G_Uncertainty();
			uncertainty.setConfidence(1.0d);
			G_Entity extractedIdentifierNode = new EntityHelper(id,
					getEntityTags(), prov, uncertainty, getProperties());
			list.add(postProcessEntity(extractedIdentifierNode));
		}
		return list;
	}

	/**
	 * Override in concrete classes to add additional properties to this entity.
	 * 
	 * @param extractedIdentifierNode
	 * @return
	 */
	public G_Entity postProcessEntity(G_Entity extractedIdentifierNode) {
		return extractedIdentifierNode;
	}
}