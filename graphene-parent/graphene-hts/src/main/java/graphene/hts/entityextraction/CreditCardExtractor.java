package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.util.validator.ValidationUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Matches major credit cards including: Visa (length 16, prefix 4), Mastercard
 * (length 16, prefix 51-55), Discover (length 16, prefix 6011), American
 * Express (length 15, prefix 34 or 37). All 16 digit formats accept optional
 * hyphens (-) between each group of four digits.
 * 
 * @author djue
 * 
 */
public class CreditCardExtractor extends AbstractExtractor {
	/**
	 * http://www.regxlib.com/DisplayPatterns.aspx?cattabindex=3&categoryId=4
	 */
	private final static String RE = "(((4\\d{3})|(5[1-5]\\d{2})|(6011))-?\\d{4}-?\\d{4}-?\\d{4})|(3[4,7]\\d{13})";

	public CreditCardExtractor() {
		p = Pattern.compile(RE);
	}

	@Override
	public G_Entity postProcessEntity(G_Entity extractedIdentifierNode) {
		if (ValidationUtils.isValid(extractedIdentifierNode)
				&& ValidationUtils.isValid(extractedIdentifierNode.getUid()))
			if (extractedIdentifierNode.getUid().startsWith("4")) {
				extractedIdentifierNode.put("CC Brand", "VISA");
			} else if (extractedIdentifierNode.getUid().startsWith("5")) {
				extractedIdentifierNode.put("CC Brand", "MASTERCARD");
			} else if (extractedIdentifierNode.getUid().startsWith("6")) {
				extractedIdentifierNode.put("CC Brand", "DISCOVER");
			} else {
				extractedIdentifierNode.put("CC Brand", "UNKNOWN");
			}
		return super.postProcessEntity(extractedIdentifierNode);
	}

	@Override
	public String getIdType() {
		return "Potential Credit Card";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.ACCOUNT.name();
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.COMMERCIAL_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential Credit Card";
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

	@Override
	public String postProcessMatch(String match) {
		if (ValidationUtils.isValid(match)) {
			return match.replaceAll("-", "");
		} else {
			return match;
		}

	}
}
