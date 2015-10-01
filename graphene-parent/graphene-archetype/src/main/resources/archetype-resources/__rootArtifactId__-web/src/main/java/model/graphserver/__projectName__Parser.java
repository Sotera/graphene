#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.graphserver;

import graphene.dao.HyperGraphBuilder;
import graphene.dao.es.impl.BasicParserESImpl;
import graphene.hts.entityextraction.Extractor;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.StringUtils;
import graphene.util.Triple;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mil.darpa.vande.generic.V_GenericNode;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.URLEncoder;
import org.slf4j.Logger;

/**
 * This is the parent class for any type of ${projectName} documents to parse. If you
 * have more than one type of document, this class can act as a repository for
 * common constants, fields and utilities for all the document parsers you may
 * create.
 * 
 * Currently ${projectName} has one document type, {@link Media}.
 * 
 * @author djue
 * @param <T>
 * 
 */
public abstract class ${projectName}Parser<T> extends BasicParserESImpl<T> {
	public static final String MEDIA_ID = "mediaid";
	public static final String MEDIA_LABEL = "medialabel";
	public static final String MEDIA_LINK = "medialink";
	public static final String MEDIA_OWNER = "mediaowner";
	public static final String MEDIA_CREATED_TIME = "mediacreatedtime";
	public static final String MEDIA_CAPTION_TEXT = "mediacaptiontext";
	public static final String MEDIA_LIKE_COUNT = "medialikecount";
	public static final String MEDIA_COMMENT_COUNT = "mediacommentcount";
	public static final String MEDIA_LOCATION_LATLON = "medialocationlatlon";
	public static final String MEDIA_LOCATION_NAME = "medialocationname";
	public static final String MEDIA_THUMBNAIL = "mediathumbnail";
	public static final String ATS_IN_COMMENTS = "atsincomments";
	public static final String ATS_IN_CAPTION = "atsincaption";
	public static final String ALL_ATS = "allats";
	public static final String HASHTAGS_IN_COMMENTS = "hashtagsincomments";
	public static final String HASHTAGS_IN_CAPTION = "hashtagsincaption";
	public static final String ALL_HASHTAGS = "allhashtags";

	/**
	 * You require the query generated from this object to have a low priority
	 * based on this objects type. (HIGH FALLOFF)
	 */
	public static final double LOW_PRIORITY = 0.1d;
	/**
	 * You require the query generated from this object to have a medium
	 * priority based on this objects type. (STANDARD FALLOFF)
	 */
	public static final double MED_PRIORITY = 0.5d;
	/**
	 * You require the query generated from this object to have a high priority
	 * based on this objects type. (NO FALLOFF)
	 */
	public static final double HIGH_PRIORITY = 1.0d;

	/**
	 * You require the results generated from a query on this object to have a
	 * high minimum certainty (CONSERVATIVE)
	 */
	public static final double HIGH_MINIMUM_CERTAINTY = 1.0d;
	/**
	 * You require the results generated from a query on this object to have a
	 * medium minimum certainty (MODERATE)
	 */
	public static final double MED_MINIMUM_CERTAINTY = 0.60d;

	/**
	 * You require the results generated from a query on this object to have a
	 * low minimum certainty (AGGRESSIVE)
	 */
	public static final double LOW_MINIMUM_CERTAINTY = .35d;
	public static final double CERTAIN = 100.0d;
	@Inject
	protected URLEncoder encoder;
	private HashMap<String, Extractor> extractorMap;
	private ArrayList<Extractor> extractors;

	@Inject
	protected Logger logger;

	@Inject
	protected HyperGraphBuilder phgb;

	protected String reportLinkTitle = "1) Open Media";

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_FREE_TEXT_EXTRACTION)
	private boolean enableFreeTextExtraction;

	Set<Triple<String, String, String>> subjectAddressList;

	// cid
	Set<Triple<String, String, String>> subjectCIDList;

	// ids
	Set<Triple<String, String, String>> subjectIDList;
	// names
	Set<Triple<String, String, String>> subjectNameList;
	Set<String> datesOfEvent;

	Set<String> datesReceived;

	Set<String> datesFiled;

	public ${projectName}Parser() {
		// extractors = new ArrayList<Extractor>();
		// extractors.add(new EmailExtractor());
		// extractors.add(new PhoneExtractor());
		// extractors.add(new URLExtractor());
		// extractors.add(new USSSNExtractor());
		// extractors.add(new CreditCardExtractor());
		// extractors.add(new MexicoRFCExtractor());
		// extractors.add(new AccountExtractor());
		//
		// extractorMap = new HashMap<String, Extractor>();
		// final EmailExtractor emailExtractor = new EmailExtractor();
		// extractorMap.put(emailExtractor.getNodetype(), emailExtractor);
		// final PhoneExtractor phoneExtractor = new PhoneExtractor();
		// extractorMap.put(phoneExtractor.getNodetype(), phoneExtractor);
		// final URLExtractor urlExtractor = new URLExtractor();
		// extractorMap.put(urlExtractor.getNodetype(), urlExtractor);
		// final USSSNExtractor usssnExtractor = new USSSNExtractor();
		// extractorMap.put(usssnExtractor.getNodetype(), usssnExtractor);
		// final CreditCardExtractor creditCardExtractor = new
		// CreditCardExtractor();
		// extractorMap.put(creditCardExtractor.getNodetype(),
		// creditCardExtractor);
		// final MexicoRFCExtractor mexicoRFCExtractor = new
		// MexicoRFCExtractor();
		// extractorMap.put(mexicoRFCExtractor.getNodetype(),
		// mexicoRFCExtractor);
		// final AccountExtractor accountExtractor = new AccountExtractor();
		// extractorMap.put(accountExtractor.getNodetype(), accountExtractor);

	}

	protected void addSafeDate(final Set<String> dates, final Object d, final String type) {
		if (ValidationUtils.isValid(d)) {
			dates.add(graphene.util.StringUtils.coalesc(" ", type, d.toString()));
		}
	}

	protected void addSafePropertyWithTitle(final String key, final String title, final Map<String, Object> map,
			final String... s) {
		final String coalesc = graphene.util.StringUtils.coalesc(" ", s);
		if (ValidationUtils.isValid(coalesc)) {
			map.put(key, StringUtils.coalesc(" ", title, coalesc));
		}
		return;
	}

	protected Collection<String> addSafeString(final Collection<String> c, final Object... s) {

		final String coalesc = graphene.util.StringUtils.coalesc(" ", s);
		if (ValidationUtils.isValid(coalesc)) {
			c.add(coalesc);
		}
		return c;
	}

	protected Collection<Triple<String, String, String>> addSafeString(final String nodeType,
			final Collection<Triple<String, String, String>> c, final String... s) {
		final String coalesc = graphene.util.StringUtils.coalesc(" ", s);
		if (ValidationUtils.isValid(coalesc)) {
			// node type, label, search value
			c.add(new Triple<String, String, String>(nodeType, coalesc, coalesc));
		}
		return c;
	}

	/**
	 * This is an easy way to make something like "Address 123 South Street",
	 * where Address is the title. IFF there were valid values in 's', then we
	 * make that value and prepend the title.
	 * 
	 * @param title
	 * @param c
	 * @param s
	 * @return
	 */
	protected Collection<Triple<String, String, String>> addSafeStringWithTitle(final String nodeType,
			final String title, final Collection<Triple<String, String, String>> c, final String... s) {
		final String coalesc = graphene.util.StringUtils.coalesc(" ", s);
		if (ValidationUtils.isValid(coalesc)) {
			// node type, label, search value
			c.add(new Triple<String, String, String>(nodeType, StringUtils.coalesc(" ", title + ":", coalesc), coalesc));
		}
		return c;
	}

	public Collection<G_Entity> createExtractedEntities(final Collection<String> ids, final Extractor extractor,
			final V_GenericNode attachTo) {
		final Collection<G_Entity> entities = Collections.EMPTY_LIST;
		for (final String id : ids) {

			final Collection<G_Entity> newEntities = extractor.extractEntities(id);

			phgb.createOrUpdateNode(id, extractor.getIdType(), extractor.getNodetype(), attachTo,
					extractor.getRelationType(), extractor.getRelationValue());

			// if (ValidationUtils.isValid(attachTo, newEntities)) {
			// logger.debug("Extracted " + extractor.getNodetype() + ": " + id
			// + " from narrative of " + attachTo.getId());
			// } else {
			// logger.warn("Creating Extracted nodes for an invalid root node!!");
			// }
			entities.addAll(newEntities);
		}
		return entities;
	}

	public void createExtractedNodes(final Collection<String> ids, final Extractor extractor,
			final V_GenericNode attachTo) {
		for (final String id : ids) {
			final V_GenericNode extractedIdentifierNode = phgb.createOrUpdateNode(HIGH_MINIMUM_CERTAINTY, 1.0,
					LOW_PRIORITY, id, extractor.getIdType(), extractor.getNodetype(), attachTo,
					extractor.getRelationType(), extractor.getRelationValue(), 50.0);

			if (ValidationUtils.isValid(attachTo, extractedIdentifierNode)) {

				logger.debug("Extracted " + extractor.getNodetype() + ": " + id + " from narrative of "
						+ attachTo.getId());
			} else {
				logger.warn("Creating Extracted nodes for an invalid root node!!");
			}
			phgb.buildQueryForNextIteration(extractedIdentifierNode);
		}
	}

	protected void createNodesFromFreeText(final String text, final V_GenericNode attachTo) {
		if (enableFreeTextExtraction && ValidationUtils.isValid(text)) {
			for (final Extractor e : extractors) {
				createExtractedNodes(e.extract(text), e, attachTo);
			}
		}
	}

	public HashMap<String, Extractor> getExtractorMap() {
		return extractorMap;
	}

	public ArrayList<Extractor> getExtractors() {
		return extractors;
	}

	public abstract String getIdFromDoc(T p);

	@Override
	public HyperGraphBuilder getPhgb() {
		return phgb;
	}

	public String getReportLabel(final T p) {
		return StringUtils.coalesc(" ", getReportType(), getIdFromDoc(p));
	}

	public String getReportLinkTitle() {
		return reportLinkTitle;
	}

	public abstract String getReportType();

	/**
	 * This is used to render a link to the report view from the EXTJS Graph
	 * view
	 * 
	 * @param page
	 * @param id
	 * @return
	 */
	protected String getReportViewerLink(final String page, final String id) {
		final String context = encoder.encode(id);
		return "<a href=${symbol_escape}"reports/" + page + "/" + context + "${symbol_escape}" class=${symbol_escape}"btn btn-primary${symbol_escape}" target=${symbol_escape}"" + id + "${symbol_escape}" >"
				+ id + "</a>";
	}

	protected void resetLists() {
		subjectAddressList = new HashSet<Triple<String, String, String>>();

		// cid
		subjectCIDList = new HashSet<Triple<String, String, String>>();

		// ids
		subjectIDList = new HashSet<Triple<String, String, String>>();
		// names
		subjectNameList = new HashSet<Triple<String, String, String>>();
		datesOfEvent = new HashSet<String>(1);

		datesReceived = new HashSet<String>(1);

		datesFiled = new HashSet<String>(1);
	}

	public void setExtractorMap(final HashMap<String, Extractor> extractorMap) {
		this.extractorMap = extractorMap;
	}

	public void setExtractors(final ArrayList<Extractor> extractors) {
		this.extractors = extractors;
	}

	@Override
	public void setPhgb(final HyperGraphBuilder phgb) {
		this.phgb = phgb;
	}

	protected void setPriorityAndScore(final double priority, final double minScore, final V_GenericNode... nodes) {

		for (final V_GenericNode n : nodes) {
			if (n != null) {
				n.setPriority(priority);
				n.setMinScore(minScore);
			}
		}
	}

	public void setReportLinkTitle(final String reportLinkTitle) {
		this.reportLinkTitle = reportLinkTitle;
	}

}
