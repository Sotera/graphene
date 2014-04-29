package graphene.hts.me;

import static graphene.util.validator.ValidationUtils.isValid;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalTruthValues;
import graphene.model.idl.G_Gender;
import graphene.model.idl.G_NodeType;
import graphene.util.Triple;
import graphene.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;

public class ParentalReferenceExtractor {

	FamilyReferenceExtractor fre = new FamilyReferenceExtractor();
	private String context = "";

	private String provenance = "";

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}

	private ArrayList<Triple<Long, G_Gender, String>> getParentNodes(
			Object names, String father, String child, String[] potentialFields) {
		HashMap<String, Object> p = new HashMap<String, Object>();

		ArrayList<Triple<Long, G_Gender, String>> list = new ArrayList<Triple<Long, G_Gender, String>>();
		if (isValid(father)) {
			// This one is not imputed since it was listed in the data as Father
			p.put(G_NodeType.NAME.toString(), father);
			p.put("FamilyRole", "Parent");// change this, we should assume
											// father,
											// but for certain it's a 'parent'
											// in a
											// parental role
			p.put("Gender", G_Gender.MALE.toString());// change this, we should
														// assume
														// father, but for
														// certain
														// it's
														// a 'parent' in a
														// parental
														// role
			p.put(G_CanonicalPropertyType.CONTEXT.toString(), context);
			p.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(),
					provenance);
			String unique = father + " Parent of " + child;
			p.put(G_CanonicalPropertyType.LINK.toString(), unique);
			Long listedFather = getOrCreateNodeId(G_NodeType.NAME.toString(),
					father, p, names);
			if (listedFather != null) {
				Triple<Long, G_Gender, String> retval = new Triple<Long, G_Gender, String>(
						listedFather, (G_Gender) p.get("Gender"),
						(String) p.get(G_NodeType.NAME.toString()));
				list.add(retval);
			}
		}
		for (String s : potentialFields) {
			Triple<Long, G_Gender, String> t = null;
			if ((t = imputeParent(names, child, s)) != null) {
				list.add(t);
			}
		}
		return list;
	}

	/**
	 * 
	 * @param names
	 * @param child
	 * @param list
	 * @param s
	 * @return
	 */
	private Triple<Long, G_Gender, String> imputeParent(Object names,
			String child, String s) {
		HashMap<String, Object> p;
		// No clean way to influence the 'child' node--we should create the
		// parent nodes first, and then hang on to all the imputed gender
		// scores.
		Tuple<String, G_Gender> t = fre.getParentAndImputedChildGender(s);
		if (t != null) {
			p = new HashMap<String, Object>();
			p.put(G_NodeType.NAME.toString(), t.getFirst());
			p.put(G_CanonicalPropertyType.METRIC_IMPUTED.toString(),
					G_CanonicalTruthValues.TRUE.toString());
			p.put(G_CanonicalPropertyType.METRIC_IMPUTEDFROM.toString(),
					"Address1");
			p.put(G_CanonicalPropertyType.CONTEXT.toString(), context);
			p.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(),
					provenance);
			String unique = t.getFirst() + " Parent of " + child;
			p.put(G_CanonicalPropertyType.LINK.toString(), unique);
			Long fId = getOrCreateNodeId(G_NodeType.NAME.toString(), unique, p,
					names);
			if (fId != null) {
				Triple<Long, G_Gender, String> retval = new Triple<Long, G_Gender, String>(
						fId, t.getSecond(), t.getFirst());
				return retval;
			}
		}
		return null;
	}

	/**
	 * Supplied by ingest class
	 * 
	 * @param string
	 * @param father
	 * @param p
	 * @param names
	 * @return
	 */
	private Long getOrCreateNodeId(String string, String father,
			HashMap<String, Object> p, Object names) {
		// TODO Auto-generated method stub
		return null;
	}
}
