package ${package}.model.graphserver;

import graphene.dao.DocumentGraphParser;
import ${package}.model.media.CommentData;
import ${package}.model.media.LikeData;
import ${package}.model.media.Media;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.query.EntityQuery;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Map;

import mil.darpa.vande.generic.V_GenericNode;

public class MediaGraphParser extends AbstractDocumentGraphParser<Media> {

	public MediaGraphParser() {
		supported = new ArrayList<String>(1);
		supported.add(Media.class.getCanonicalName());
		parenting = false;
	}

	@Override
	public Map<String, Object> getAdditionalProperties(final Object obj) {
		if (!(obj instanceof Media)) {
			return null;
		}
		final Media p = (Media) obj;
		return p.getAdditionalProperties();
	}

	@Override
	public String getReportId(final Media p) {
		// return p.getDETAILS().getCurrentBsaIdentifier().toString();
		return p.getId();
	}

	@Override
	public String getReportType() {
		return "MEDIA";
	}

	// This method creates a sub graph of the nodes inside a report, and a list
	// of new identifiers to search on.
	@Override
	public boolean parse(final Object obj, final EntityQuery q) {
		if (!(obj instanceof Media)) {
			return false;
		}
		Media p = (Media) obj;

		// Make nodes dealing with the report itself.
		if (ValidationUtils.isValid(p)) {

			final String reportId = p.getId();

			// Don't scan the same object twice!
			if (phgb.isPreviouslyScannedResult(reportId)) {
				return false;
			}
			p = populateExtraFields(p, q);
			final double inheritedScore = (double) p.getAdditionalProperties().get(DocumentGraphParser.SCORE);

			phgb.addScannedResult(reportId);
			// report node does not attach to anything.
			final V_GenericNode reportNode = phgb.createOrUpdateNode(reportId, G_CanonicalPropertyType.MEDIA.name(),
					G_CanonicalPropertyType.MEDIA.name(), null, null, null);
			reportNode.setLabel((String) p.getAdditionalProperties().get(MEDIA_LABEL));
			reportNode.addData("Type", (String) p.getAdditionalProperties().get(REPORT_TYPE));

			// reportNode.addData(reportLinkTitle,
			// getReportViewerLink("BSARReport", reportId));
			reportNode.addData(reportLinkTitle, "<a href=\"" + p.getLink() + "\" class=\"btn btn-primary\" target=\""
					+ p.getId() + "\" >" + p.getId() + "</a>");

			phgb.addReportDetails(reportNode, p.getAdditionalProperties());

			phgb.addGraphQueryPath(reportNode, q);

			if (ValidationUtils.isValid(p.getUsername())) {
				final V_GenericNode ownerNode = phgb.createOrUpdateNode(p.getUsername(),
						G_CanonicalPropertyType.USERNAME.name(), G_CanonicalPropertyType.USERNAME.name(), reportNode,
						G_CanonicalRelationshipType.OWNER_OF.name(), G_CanonicalRelationshipType.OWNER_OF.name());
				phgb.buildQueryForNextIteration(ownerNode);
			}

			// createNodesFromFreeText(p.getCaptionText(), reportNode);

			// V_GenericNode ipNode = null;
			// final V_GenericNode marketNode = null;
			// final V_GenericNode commodityNode = null;

			for (int i = 0; i < p.getComments().getCommentsData().size(); i++) {
				final CommentData comment = p.getComments().getCommentsData().get(i);
				final String commentId = reportNode.getId() + "-comment-" + i;

				if (ValidationUtils.isValid(comment)) {
					V_GenericNode commentNode = null, commenterNode = null;
					commentNode = phgb.createOrUpdateNode(commentId, "Comment", "Comment", reportNode,
							G_CanonicalRelationshipType.PART_OF.name(), G_CanonicalRelationshipType.PART_OF.name());

					if (ValidationUtils.isValid(commentNode)) {
						commentNode.addData("Comment Text", comment.getText());
						commentNode.setLabel(comment.getTextSample());
					}

					if (ValidationUtils.isValid(comment.getUsername())) {
						commenterNode = phgb.createOrUpdateNode(comment.getUsername(),
								G_CanonicalPropertyType.USERNAME.name(), G_CanonicalPropertyType.USERNAME.name(),
								commentNode, G_CanonicalRelationshipType.OWNER_OF.name(),
								G_CanonicalRelationshipType.OWNER_OF.name());
						phgb.buildQueryForNextIteration(commenterNode);
					}
				}
			}

			for (int i = 0; i < p.getLikes().getLikesData().size(); i++) {
				final LikeData like = p.getLikes().getLikesData().get(i);

				if (ValidationUtils.isValid(like)) {

					V_GenericNode likerNode = null;
					if (ValidationUtils.isValid(like.getUsername())) {
						likerNode = phgb.createOrUpdateNode(like.getUsername(),
								G_CanonicalPropertyType.USERNAME.name(), G_CanonicalPropertyType.USERNAME.name(),
								reportNode, G_CanonicalRelationshipType.LIKES.name(),
								G_CanonicalRelationshipType.LIKES.name());
						phgb.buildQueryForNextIteration(likerNode);
					}
				}
			}
		}

		return true;
	}

	@Override
	public Media populateExtraFields(final Media p, final EntityQuery sq) {

		p.setAdditionalProperty(MEDIA_LABEL, getReportLabel(p));
		p.setAdditionalProperty(MEDIA_ID, p.getId());
		p.setAdditionalProperty(MEDIA_LINK, p.getLink());
		p.setAdditionalProperty(MEDIA_OWNER, p.getUsername());
		p.setAdditionalProperty(MEDIA_CREATED_TIME, p.getCreatedTime());
		p.setAdditionalProperty(MEDIA_CAPTION_TEXT, p.getCaptionText());
		p.setAdditionalProperty(MEDIA_LIKE_COUNT, p.getLikes().getCount());
		p.setAdditionalProperty(MEDIA_COMMENT_COUNT, p.getComments().getCount());
		p.setAdditionalProperty(MEDIA_THUMBNAIL, p.getThumbnail());

		if ((p.getLocation().getLatitude() != null) && (p.getLocation().getLongitude() != null)) {
			p.setAdditionalProperty(MEDIA_LOCATION_LATLON, p.getLocation().getLatitude() + ", "
					+ p.getLocation().getLongitude());
		}

		if (ValidationUtils.isValid(p.getLocation().getLatitude())
				&& ValidationUtils.isValid(p.getLocation().getLongitude())) {
			p.setAdditionalProperty(MEDIA_LOCATION_LATLON, p.getLocation().getLatitude() + ", "
					+ p.getLocation().getLongitude());
		}

		if (ValidationUtils.isValid(p.getLocation().getName())) {
			p.setAdditionalProperty(MEDIA_LOCATION_NAME, p.getLocation().getName());
		}

		// FIXME: using literal strings instead of strings defined in
		// AbstractDocumentParser
		p.setAdditionalProperty("ATS_IN_CAPTION", p.getAtsInCaption());
		p.setAdditionalProperty("ATS_IN_COMMENTS", p.getAtsInComments());
		p.setAdditionalProperty("HASHTAGS_IN_CAPTION", p.getHashTagsInCaption());
		p.setAdditionalProperty("HASHTAGS_IN_COMMENTS", p.getHashTagsInComments());

		return p;
	}
}
