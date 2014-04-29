package graphene.hts.me;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalTruthValues;
import graphene.model.idl.G_Gender;
import graphene.model.idl.G_NodeType;
import graphene.model.idl.G_RelationshipType;
import graphene.util.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Moved familial imputing to it's own class, outside of the main ingest class.
 * 
 * @author djue
 * 
 */
public class ImputeRelationships {
	public void impute(
			ArrayList<Triple<Long, G_Gender, String>> fatherTriples, Object customerId1, Object relationship_bf, Object hasKinRels, Object customerId2, Object customers) {
		// Deal with Familial Parents of the node

		int childIsMaleVote = 0;
		int childIsFemaleVote = 0;
		int childIsUnknownVote = 0;
		for (Triple<Long, G_Gender, String> imputedFatherTriple : fatherTriples) {
			// properties for the RELATION object, not the nodes.
			Map<String, Object> fprops = new HashMap<String, Object>();
			fprops.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(), "Dataset1");
			fprops.put(G_CanonicalPropertyType.CONTEXT.toString(), "Parent Of");
			fprops.put(G_CanonicalPropertyType.METRIC_IMPUTED.toString(),
					G_CanonicalTruthValues.TRUE.toString());
			createSafeRelationship(imputedFatherTriple.getFirst(),
					G_RelationshipType.HAS_KIN, customerId1, fprops,
					relationship_bf, hasKinRels);
			createSafeRelationship(imputedFatherTriple.getFirst(),
					G_RelationshipType.HAS_KIN, customerId2, fprops,
					relationship_bf, hasKinRels);
			/*
			 * This below is a tricky experimental part, so hold on to your
			 * mouse:
			 * 
			 * So we've created a bunch of father name nodes, some of which may
			 * be imputed. They are also uniquely identified as "Parent of "
			 * <customer name>.
			 * 
			 * Now what we intend to do is get or create real name nodes for
			 * these imputed fathers nodes, and then create a relationship to
			 * them saying they may be the same person. This creates a
			 * disconnect between 'Bob father of Sam' and 'Bob father of Mary'
			 * with a real Bob already in the graph. Rather than directly
			 * linking all the father Bobs directly to Bob an actual account
			 * holder, now we'll have Bob the actual account holder have
			 * 'MAY_BE_THE_SAME' relationship to each 'Bob father of x' node.
			 * 
			 * I don't know for sure if this will be a good design choice, but I
			 * feel like there will be multiple occurrences where we want a 'May
			 * be the same' relationship, without looking at properties to see
			 * how something was imputed.
			 */
			Map<String, Object> mbtsprops = new HashMap<String, Object>();
			mbtsprops.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(), "Dataset1");
			mbtsprops.put(G_CanonicalPropertyType.CONTEXT.toString(), "Parent Of");
			mbtsprops.put(G_CanonicalPropertyType.METRIC_IMPUTED.toString(),
					G_CanonicalTruthValues.TRUE.toString());
			Long nonImputedFatherId = getOrCreateNodeId(
					G_NodeType.NAME.toString(),
					imputedFatherTriple.getThird(), mbtsprops, customers);
			createSafeRelationship(imputedFatherTriple.getFirst(),
					G_RelationshipType.MAY_BE_THE_SAME, nonImputedFatherId,
					mbtsprops, relationship_bf, hasKinRels);

			if (imputedFatherTriple.getSecond().equals(G_Gender.MALE)) {

				childIsMaleVote++;
			} else if (imputedFatherTriple.getSecond().equals(G_Gender.MALE)) {

				childIsFemaleVote++;
			} else if (imputedFatherTriple.getSecond().equals(G_Gender.UKNOWN)) {

				childIsUnknownVote++;
			}
		}
		// Deal with imputed sex of main node here

		HashMap<String, Object> nameNodeImputedProperties = new HashMap<String, Object>();
		int totalVotes = childIsMaleVote + childIsFemaleVote
				+ childIsUnknownVote;
		if (totalVotes > 0) {
			nameNodeImputedProperties.put(ImputedScoreType.MALE.toString(),
					G_CanonicalTruthValues.TRUE.toString());

			double scoreMale = childIsMaleVote / totalVotes;
			double scoreFemale = childIsMaleVote / totalVotes;
			double scoreUnknown = childIsMaleVote / totalVotes;
		}

	}

	private Long getOrCreateNodeId(String string, String third,
			Map<String, Object> mbtsprops, Object customers) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createSafeRelationship(Long first, G_RelationshipType hasKin,
			Object customerId1, Map<String, Object> fprops,
			Object relationship_bf, Object hasKinRels) {
		// TODO Auto-generated method stub
		
	}
}
