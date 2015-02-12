package graphene.web.pages;

import graphene.dao.CombinedDAO;
import graphene.dao.DataSourceListDAO;
import graphene.dao.DocumentGraphParser;
import graphene.dao.StyleService;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.model.query.EntityQuery;
import graphene.model.view.GrapheneResults;
import graphene.services.HyperGraphBuilder;
import graphene.services.LinkGenerator;
import graphene.util.DataFormatConstants;
import graphene.util.Triple;
import graphene.util.Tuple;
import graphene.util.stats.TimeReporter;
import graphene.util.validator.ValidationUtils;
import graphene.web.annotations.PluginPage;
import graphene.web.model.CombinedEntityDataSource;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.got5.tapestry5.jquery.ImportJQueryUI;
import org.slf4j.Logger;

/**
 * This will be interesting now that we're using G_Properties. We may need
 * special rendering blocks that take the properties and render them based on
 * the internals of the properties
 * 
 * For instance, if the range is a single value and a String, display like a
 * string.
 * 
 * If the range is something with a beginning and end, render it differently
 * based on the type (like a date range or value range)
 * 
 * Also, we can exploit any other information in the property, like provenance,
 * uncertainty and other tags.
 * 
 * @author djue
 * 
 */
@PluginPage(visualType = G_VisualType.HIDDEN, menuName = "Combined Search", icon = "fa fa-lg fa-fw fa-search")
@Import(stylesheet = { "context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:/core/js/libs/jquery/jquery-ui-1.10.3.min.js")
public class CombinedEntitySearchPage extends SimpleBasePage implements LinkGenerator {
	@Inject
	private G_UserDataAccess userDataAccess;
	@Inject
	private BeanModelSource beanModelSource;

	@Property
	private Triple<String, String, String> currentAddress;
	@Property
	private Triple<String, String, String> currentCommunicationId;
	@Property
	private String currentDate;
	@Property
	private Map<String, Object> currentEntity;

	@Property
	private Tuple<String, String> currentIcon;

	@Property
	private Triple<String, String, String> currentIdentifier;

	@Property
	private Triple<String, String, String> currentName;

	@Property
	private Tuple<String, String> currentAt;
	
	@Property
	private Tuple<String, String> currentHashTag;
	
	@Inject
	private CombinedDAO dao;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Property
	private final GridDataSource gds = new CombinedEntityDataSource(dao);

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	@Inject
	@Symbol(SymbolConstants.APPLICATION_FOLDER)
	private String path;

	@Inject
	private HyperGraphBuilder<Object> phgb;

	@Property
	private List<Map<String, Object>> populatedTableResults;

	private String previousSearchValue;

	@Property
	private GrapheneResults<Object> results;
	@Property
	private int resultShowingCount;

	@Property
	private int resultTotalCount;
	/**
	 * The overall schema, such as an ES index
	 */
	@ActivationRequestParameter(value = "schema")
	@Property
	private String searchSchema;
	/**
	 * Aka subschema aka the type a user would usually select between, such as
	 * an ES type
	 */
	@ActivationRequestParameter(value = "type")
	@Property
	private String searchTypeFilter;
	@ActivationRequestParameter(value = "maxResults")
	@Property
	private long maxResults;
	/**
	 * The value the user types in
	 */
	@ActivationRequestParameter(value = "term")
	private String searchValue;

	@InjectPage
	private CombinedEntitySearchPage searchPage;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	private Integer defaultMaxResults;
	/**
	 * The type of query to run
	 */
	@ActivationRequestParameter(value = "match")
	private String searchMatch;

	@Property
	private Object selectedEvent;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Inject
	@Symbol(G_SymbolConstants.EXT_PATH)
	private String extPath;

	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	@Property
	private boolean currentSelectedWorkspaceExists;
	@Property
	private String currentSearchValue;

	@Inject
	private StyleService style;

	public Collection<Triple<String, String, String>> getAddressList() {
		return (Collection<Triple<String, String, String>>) currentEntity.get(DocumentGraphParser.SUBJECTADDRESSLIST);
	}

	public Link getAddressPivotLink(final String term) {
		// XXX: pick the right search type based on the link value
		final Link l = searchPage.set(null, null, G_SearchType.COMPARE_EQUALS.name(), term, defaultMaxResults);
		return l;
	}

	public Double getAmount() {
		return (Double) currentEntity.get(DocumentGraphParser.TOTALAMOUNTNBR);
	}
	
	public String getFormattedAmount() {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		String amount = formatter.format(getAmount());
		//if (amount.charAt(0) == '$') {
		//	amount = amount.substring(1, amount.length());
		//}
		return amount;
	}

	public Collection<Triple<String, String, String>> getCIdentifierList() {
		return (Collection<Triple<String, String, String>>) currentEntity.get(DocumentGraphParser.SUBJECTCIDLIST);
	}

	public Format getDateFormat() {
		return new SimpleDateFormat(getDatePattern());
	}

	public String getDatePattern() {
		return DataFormatConstants.DATE_FORMAT_STRING;
	}

	/**
	 * 
	 * @param type
	 * @param value
	 * @param searchValue2
	 * @return
	 */
	private GrapheneResults<Object> getEntities(final String schema, final String subType, final String matchType,
			final String value, final int maxResults) {
		GrapheneResults<Object> metaresults = null;
		if (ValidationUtils.isValid(value)) {
			final EntityQuery sq = new EntityQuery();
			G_SearchType g_SearchType = null;
			if (ValidationUtils.isValid(matchType)) {
				try {
					g_SearchType = G_SearchType.valueOf(matchType);
				} catch (final Exception e) {
					g_SearchType = G_SearchType.COMPARE_CONTAINS;
				}
			}
			final G_SearchTuple<String> gs = new G_SearchTuple<String>(value, g_SearchType);
			if (ValidationUtils.isValid(subType) && !subType.contains(DataSourceListDAO.ALL_REPORTS)) {
				sq.getFilters().addAll(graphene.util.StringUtils.tokenizeToStringCollection(subType, ","));
			}
			sq.addAttribute(gs);
			sq.setMaxResult(maxResults);
			sq.setSchema(schema);
			sq.setMinimumScore(0.0);
			if (isUserExists()) {
				sq.setUserId(getUser().getId());
				sq.setUserName(getUser().getUsername());
			}

			try {
				loggingDao.recordQuery(sq);
				if (currentSelectedWorkspaceExists) {
					List<EntityQuery> qo = currentSelectedWorkspace.getQueryObjects();
					if (qo == null) {
						qo = new ArrayList<EntityQuery>(1);
					}
					qo.add(sq);
					currentSelectedWorkspace.setQueryObjects(qo);

					userDataAccess.saveWorkspace(getUser().getId(), currentSelectedWorkspace);
				}
				metaresults = dao.findByQueryWithMeta(sq);
				final TimeReporter tr = new TimeReporter("parsing details of results", logger);
				populatedTableResults = new ArrayList<Map<String, Object>>();
				// Populate all the results!
				for (final Object m : metaresults.getResults()) {
					final DocumentGraphParser parserForObject = phgb.getParserForObject(m);
					if (parserForObject != null) {
						parserForObject.populateExtraFields(m, sq);
						populatedTableResults.add(parserForObject.getAdditionalProperties(m));
					} else {
						logger.error("Could not find parser for " + m);
					}
				}
				tr.logAsCompleted();
			} catch (final Exception e) {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, e.getMessage());
				e.printStackTrace();
			}
		}
		if ((metaresults == null) || (metaresults.getNumberOfResultsReturned() == 0)) {
			alertManager.alert(Duration.TRANSIENT, Severity.INFO, "No results found for " + value + ".");
			resultShowingCount = 0;
			resultTotalCount = 0;
		} else {
			alertManager.alert(
					Duration.TRANSIENT,
					Severity.SUCCESS,
					"Showing " + metaresults.getNumberOfResultsReturned() + " of "
							+ metaresults.getNumberOtResultsTotal() + " results found.");
		}
		return metaresults;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getExtLink() {
		return extPath + getMediaId();
	}

	/**
	 * Get a list of icons that apply to this report.
	 * 
	 * @return
	 */
	public Collection<Tuple<String, String>> getIconList() {
		return (Collection<Tuple<String, String>>) currentEntity.get(DocumentGraphParser.ICONLIST);
	}

	public Collection<Triple<String, String, String>> getIdentifierList() {
		return (Collection<Triple<String, String, String>>) currentEntity.get(DocumentGraphParser.SUBJECTIDLIST);
	}

	public BeanModel getModel() {
		final BeanModel<Object> model = beanModelSource.createEditModel(Object.class, messages);
		model.addEmpty("rank");
		model.addEmpty("actions");
		model.addEmpty("username");
		model.addEmpty("createdTime");
		model.addEmpty("captionText");
		model.addEmpty("likeCount");
		model.addEmpty("commentCount");
		model.addEmpty("hashtags");
		model.addEmpty("ats");
		model.addEmpty("location");

		model.getById("rank").sortable(true);
		model.getById("actions").sortable(false);
		model.getById("username").sortable(true);
		model.getById("createdTime").sortable(true);
		model.getById("captionText").sortable(true);
		model.getById("likeCount").sortable(true);
		model.getById("commentCount").sortable(true);
		model.getById("hashtags").sortable(true);
		model.getById("ats").sortable(true);
		model.getById("location").sortable(true);

		return model;
	}

	public Format getMoneyFormat() {
		return DataFormatConstants.getMoneyFormat();
	}

	public Collection<Triple<String, String, String>> getNameList() {
		return (Collection<Triple<String, String, String>>) currentEntity.get(DocumentGraphParser.SUBJECTNAMELIST);

	}

	// /////////////////////////////////////////////////////////////////////
	// FILTER
	// /////////////////////////////////////////////////////////////////////

	public Link getNamePivotLink(final String term) {
		// XXX: pick the right search type based on the link value
		final Link l = searchPage.set(null, "media", G_SearchType.COMPARE_EQUALS.name(), term, defaultMaxResults);
		return l;
	}

	public JSONObject getOptions() {

		final JSONObject json = new JSONObject(
			"bJQueryUI", "true",
			"bAutoWidth", "true",
			"sDom", "<\"col-sm-4\"f><\"col-sm-4\"i><\"col-sm-4\"l><\"row\"<\"col-sm-12\"p><\"col-sm-12\"r>><\"row\"<\"col-sm-12\"t>><\"row\"<\"col-sm-12\"ip>>"
		);
		// Sort by score then by date.
		json.put("aaSorting", new JSONArray().put(new JSONArray().put(0).put("asc")).put(new JSONArray().put(3).put("desc")));

		final JSONArray columnArray = new JSONArray();

		// a two-dimensional array that acts as a definition and mapping between
		// column headers and their widths (in %)
		final String[][] properties = { { "rank", "1%" }, { "actions", "10%" }, { "informationIcons", "8%" },
				{ "date", "7%" }, { "amount", "7%" }, { "subjects", "12%" }, { "addressList", "25%" },
				{ "communicationIdentifierList", "15%" }, { "identifierList", "15%" } };

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "numeric"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "string"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "false", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "html"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "date"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"type", "currency",
			"sType", "currency"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "string"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "string"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "string"
		));

		columnArray.put(new JSONObject(
			"mDataProp", properties[columnArray.length()][0], 
			"bSortable", "true", 
			"sWidth", properties[columnArray.length()][1], 
			"sType", "string"
		));

		//json.put("sScrollX", "100%");
		//json.put("bScrollCollapse", false);
//		json.put("aoColumns", columnArray);
		json.put("oLanguage", new JSONObject("sSearch", "Filter:"));
		
		return json;
	}

	public Link getPivotLink(final String term) {
		final Link l = searchPage.set(null, null, G_SearchType.COMPARE_CONTAINS.name(), term, defaultMaxResults);
		return l;
	}

	public int getRank() {
		return (int) currentEntity.get(DocumentGraphParser.CARDINAL_ORDER);
	}

	public String getReportId() {
		return (String) currentEntity.get(DocumentGraphParser.REPORT_ID);
	}
	
	public String getMediaId() {
		return (String) currentEntity.get(DocumentGraphParser.MEDIA_ID);
	}

	public String getReportPageLink() {
		return (String) currentEntity.get(DocumentGraphParser.REPORT_LINK);
	}
	
	public String getMediaPageLink() {
		return (String) currentEntity.get(DocumentGraphParser.MEDIA_LINK);
	}
	
	public String getMediaOwner() {
		return (String) currentEntity.get(DocumentGraphParser.MEDIA_OWNER);
	}
	
	public String getMediaCaption() {
		return (String) currentEntity.get(DocumentGraphParser.MEDIA_CAPTION_TEXT);
	}
	
	public String getMediaLikeCount() {
		return String.valueOf(currentEntity.get(DocumentGraphParser.MEDIA_LIKE_COUNT));
	}
	
	public String getMediaCommentCount() {
		return String.valueOf(currentEntity.get(DocumentGraphParser.MEDIA_COMMENT_COUNT));
	}
	
	public String getMediaLocationLatLon() {
		return (String) currentEntity.get(DocumentGraphParser.MEDIA_LOCATION_LATLON);
	}
	
	public String getMediaLocationName() {
		return (String) currentEntity.get(DocumentGraphParser.MEDIA_LOCATION_NAME);
	}

	public String getReportType() {
		return (String) currentEntity.get(DocumentGraphParser.REPORT_TYPE);
	}
	
	public Object getMediaCreatedTime() {
		return currentEntity.get(DocumentGraphParser.MEDIA_CREATED_TIME);
	}

	public String getScore() {
		final Double d = (Double) currentEntity.get(DocumentGraphParser.SCORE);
		return DataFormatConstants.formatScore(d);
	}

	public Collection<Tuple<String, String>> getAtsInComments() {
		return (Collection<Tuple<String, String>>) currentEntity.get("ATS_IN_COMMENTS");
	}
	
	public Collection<Tuple<String, String>> getAtsInCaption() {
		return (Collection<Tuple<String, String>>) currentEntity.get("ATS_IN_CAPTION");
	}
	
	public Collection<Tuple<String, String>> getHashTagsInComments() {
		return (Collection<Tuple<String, String>>) currentEntity.get("HASHTAGS_IN_COMMENTS");
	}
	
	public Collection<Tuple<String, String>> getHashTagsInCaption() {
		return (Collection<Tuple<String, String>>) currentEntity.get("HASHTAGS_IN_CAPTION");
	}
	
	/**
	 * @return the searchValue
	 */
	public final String getSearchValue() {
		return searchValue;
	}

	public String getStyleFor(final Triple<String, String, String> currentThing) {
		boolean containsTerm = false;
		// splits on " ", "/", and "\" in the search term.
		// FIXME: tokenize based on " and use the substring between them as a single term
		String[] searchTerms = currentSearchValue.split("[ \\/]+");
		for (String term : searchTerms) {
			// term.replaceAll() looks for all " characters and removes them for the sake of comparison
			if (StringUtils.containsIgnoreCase(currentThing.getThird(), term.replaceAll("[\"]+", ""))) {
				containsTerm = true;
				break;
			}
		}
		return style.getStyle(currentThing.getFirst(), containsTerm);
	}

	@Override
	public Link set(final String schema, final String type, final String match, final String value,
			final long maxResults) {
		searchSchema = schema;
		searchTypeFilter = type;
		searchValue = value;
		searchMatch = match;
		this.maxResults = maxResults;
		return pageRenderLinkSource.createPageRenderLink(this.getClass());
	}

	/**
	 * @param searchValue
	 *            the searchValue to set
	 */
	public final void setSearchValue(final String searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * This should help with persisted values.
	 */
	@Log
	void setupRender() {
		if (ValidationUtils.isValid(searchValue)) {
			currentSearchValue = searchValue;
			try {
				results = getEntities(searchSchema, searchTypeFilter, searchMatch, currentSearchValue, (int) maxResults);

			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			results = null;
		}

	}
}
