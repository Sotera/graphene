package graphene.hts.me;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_CanonicalTruthValues;
import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_Gender;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.util.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * Moved familial imputing to it's own class, outside of the main ingest class.
 * 
 * @author djue
 * 
 */
public class ImputeRelationships {
	@Inject
	private Logger logger;
	@Inject
	private G_EdgeTypeAccess edgeTypeAccess;

	@Inject
	private G_NodeTypeAccess nodeTypeAccess;

	@Inject
	private G_PropertyKeyTypeAccess propertyKeyTypeAccess;

	private void createSafeRelationship(final Long first, final G_EdgeType hasKin, final Object customerId1,
			final Map<String, Object> fprops, final Object relationship_bf, final Object hasKinRels) {
		// TODO Auto-generated method stub

	}

	private Long getOrCreateNodeId(final G_IdType g_NodeType, final String third, final Map<String, Object> mbtsprops,
			final Object customers) {
		// TODO Auto-generated method stub
		return null;
	}

	public void impute(final ArrayList<Triple<Long, G_Gender, String>> fatherTriples, final Object customerId1,
			final Object relationship_bf, final Object hasKinRels, final Object customerId2, final Object customers) {
		// Deal with Familial Parents of the node

		int childIsMaleVote = 0;
		int childIsFemaleVote = 0;
		int childIsUnknownVote = 0;
		for (final Triple<Long, G_Gender, String> imputedFatherTriple : fatherTriples) {
			// properties for the RELATION object, not the nodes.
			final Map<String, Object> fprops = new HashMap<String, Object>();
			fprops.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(), "Dataset1");
			fprops.put(G_CanonicalPropertyType.CONTEXT.toString(), "Parent Of");
			fprops.put(G_CanonicalPropertyType.METRIC_IMPUTED.toString(), G_CanonicalTruthValues.TRUE.toString());
			try {
				createSafeRelationship(imputedFatherTriple.getFirst(),
						edgeTypeAccess.getEdgeType(G_CanonicalRelationshipType.KIN_OF.name()), customerId1, fprops,
						relationship_bf, hasKinRels);
			} catch (final AvroRemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				createSafeRelationship(imputedFatherTriple.getFirst(),
						edgeTypeAccess.getEdgeType(G_CanonicalRelationshipType.KIN_OF.name()), customerId2, fprops,
						relationship_bf, hasKinRels);
			} catch (final AvroRemoteException e) {
				logger.error(e.getMessage());
			}
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
			final Map<String, Object> mbtsprops = new HashMap<String, Object>();
			mbtsprops.put(G_CanonicalPropertyType.METRIC_PROVENANCE.toString(), "Dataset1");
			mbtsprops.put(G_CanonicalPropertyType.CONTEXT.toString(), "Parent Of");
			mbtsprops.put(G_CanonicalPropertyType.METRIC_IMPUTED.toString(), G_CanonicalTruthValues.TRUE.toString());
			Long nonImputedFatherId = null;
			try {
				nonImputedFatherId = getOrCreateNodeId(nodeTypeAccess.getNodeType(G_CanonicalPropertyType.NAME.name()),
						imputedFatherTriple.getThird(), mbtsprops, customers);
			} catch (final AvroRemoteException e) {
				logger.error(e.getMessage());
			}
			try {
				createSafeRelationship(imputedFatherTriple.getFirst(),
						edgeTypeAccess.getEdgeType(G_CanonicalRelationshipType.MAY_BE_THE_SAME.name()),
						nonImputedFatherId, mbtsprops, relationship_bf, hasKinRels);
			} catch (final AvroRemoteException e) {
				logger.error(e.getMessage());
			}

			if (imputedFatherTriple.getSecond().equals(G_Gender.MALE)) {

				childIsMaleVote++;
			} else if (imputedFatherTriple.getSecond().equals(G_Gender.MALE)) {

				childIsFemaleVote++;
			} else if (imputedFatherTriple.getSecond().equals(G_Gender.UKNOWN)) {

				childIsUnknownVote++;
			}
		}
		// Deal with imputed sex of main node here

		final HashMap<String, Object> nameNodeImputedProperties = new HashMap<String, Object>();
		final int totalVotes = childIsMaleVote + childIsFemaleVote + childIsUnknownVote;
		if (totalVotes > 0) {
			nameNodeImputedProperties.put(ImputedScoreType.MALE.toString(), G_CanonicalTruthValues.TRUE.toString());
		}

	}
}
