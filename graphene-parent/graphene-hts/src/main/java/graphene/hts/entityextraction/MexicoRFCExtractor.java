package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Registro Federal de Contribuyentes (RFC) , used in Mexico as a unique set of
 * caracters for a person or corporation registration . Registro Federal de
 * Contribuyentes utilizado en Mexico para el registro en hacienda.
 * 
 * @author djue
 * 
 */
public class MexicoRFCExtractor extends AbstractExtractor {
	/*
	 * http://www.regxlib.com/DisplayPatterns.aspx?cattabindex=3&categoryId=4
	 */
	private final static String RE_MEXICO_RFC = "(([A-Z|a-z|&amp;]{3}\\d{2}((0[1-9]|1[012])(0[1-9]|1\\d|2[0-8])|(0[13456789]|1[012])(29|30)|(0[13578]|1[02])31)|([02468][048]|[13579][26])0229)(\\w{2})([A|a|0-9]{1})$|^([A-Z|a-z]{4}\\d{2}((0[1-9]|1[012])(0[1-9]|1\\d|2[0-8])|(0[13456789]|1[012])(29|30)|(0[13578]|1[02])31)|([02468][048]|[13579][26])0229)((\\w{2})([A|a|0-9]{1})){0,3})";

	public MexicoRFCExtractor() {
		p = Pattern.compile(RE_MEXICO_RFC);
	}

	@Override
	public String getIdType() {
		return "Potential Mexican RFC";
	}

	@Override
	public String getNodetype() {
		return "Extracted"+ G_CanonicalPropertyType.GOVERNMENTID.name();
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.GOVERNMENT_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential Mexican RFC";
	}
}
