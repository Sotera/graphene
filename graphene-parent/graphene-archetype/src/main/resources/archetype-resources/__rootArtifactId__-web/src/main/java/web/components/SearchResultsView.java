#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.components;

import graphene.dao.G_Parser;
import graphene.dao.LoggingDAO;
import graphene.dao.StyleService;
import graphene.dao.es.JestModule;
import ${package}.model.graphserver.${projectName}Parser;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.model.idlhelper.ListRangeHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.idlhelper.QueryHelper;
import graphene.util.DataFormatConstants;
import graphene.util.Triple;
import graphene.util.Tuple;
import graphene.util.validator.ValidationUtils;
import graphene.web.pages.CombinedEntitySearchPage;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
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
@Import(stylesheet = { "context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:/core/js/libs/jquery/jquery-ui-1.10.3.min.js")
public class SearchResultsView {

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	protected String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_WORKSPACES)
	protected String workspacesEnabled;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	protected String appVersion;

	@Property
	@SessionState(create = false)
	protected List<G_Workspace> workspaces;

	protected boolean workspacesExists;
	@Inject
	protected LoggingDAO loggingDao;

	@Inject
	protected AlertManager alertManager;

	@Property
	@Persist
	private boolean highlightZoneUpdates;

	@SessionState(create = false)
	protected G_User user;

	protected boolean userExists;

	@Inject
	protected Request request;

	@Inject
	private G_UserDataAccess userDataAccess;

	@Inject
	private BeanModelSource beanModelSource;

	@Property
	private Triple<String, String, String> currentAddress;

	@Property
	private Tuple<String, String> currentHashTag;

	@Property
	private Tuple<String, String> currentAt;

	@Property
	private G_Property currentProperty;

	@Property
	private String currentDate;

	@Property
	private Tuple<String, String> currentIcon;

	@Property
	private Triple<String, String, String> currentIdentifier;
	@Property
	private Triple<String, String, String> currentName;
	@Inject
	private G_DataAccess dao;
	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	@Inject
	@Symbol(SymbolConstants.APPLICATION_FOLDER)
	private String path;

	@Property
	private G_SearchResults searchResults;

	@Property
	private G_SearchResult currentSearchResult;

	@Property
	private int resultShowingCount;

	@Property
	private int resultTotalCount;

	/**
	 * The overall schema, such as an ES index
	 */
	@Parameter(autoconnect = true, required = true)
	@Property
	private String searchSchema;

	/**
	 * Aka subschema aka the type a user would usually select between, such as
	 * an ES type
	 */
	@Parameter(autoconnect = true, required = true)
	@Property
	private String searchTypeFilter;

	@Parameter(autoconnect = true, required = true)
	@Property
	private long maxResults;

	/**
	 * The value the user types in
	 */
	@Parameter(autoconnect = true, required = true)
	private String searchValue;

	@InjectPage
	private CombinedEntitySearchPage searchPage;
	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	private Long defaultMaxResults;

	@Inject
	@Symbol(JestModule.ES_SEARCH_INDEX)
	private String index;

	/**
	 * The type of query to run
	 */
	@Parameter(autoconnect = true, required = true)
	@Property
	private String searchMatch;

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

	@Persist
	private BeanModel<Object> model;

	final NumberFormat formatter = NumberFormat.getCurrencyInstance();

	public Collection<Triple<String, String, String>> getAddressList() {
		return new ArrayList();
	}

	public Link getAddressPivotLink(final String term) {
		// XXX: pick the right search type based on the link value
		final Link l = searchPage.set(null, null, G_Constraint.EQUALS.name(), term, defaultMaxResults);
		return l;
	}

	public List<Tuple<String, String>> getAllAts() {
		return (List<Tuple<String, String>>) PropertyHelper.getListValue(getEntity().getProperties().get(
				${projectName}Parser.ALL_ATS));
	}

	public List<Tuple<String, String>> getAllHashTags() {
		return (List<Tuple<String, String>>) PropertyHelper.getListValue(getEntity().getProperties().get(
				${projectName}Parser.ALL_HASHTAGS));
	}

	public Double getAmount() {

		final Double d = (Double) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				G_Parser.TOTALAMOUNTNBR));
		if (d == null) {
			return 0.0d;
		} else {
			return d;
		}
	}

	public Collection<Tuple<String, String>> getAtsInCaption() {
		return (Collection<Tuple<String, String>>) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.ATS_IN_CAPTION));
	}

	public Collection<Tuple<String, String>> getAtsInComments() {
		return (Collection<Tuple<String, String>>) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.ATS_IN_COMMENTS));
	}

	public Collection<G_Property> getCIdentifierList() {
		return (Collection<G_Property>) PropertyHelper.getListValue(getEntity().getProperties().get(
				G_Parser.SUBJECTCIDLIST));
	}

	public String getDate() {
		return "mockDate";
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
	private G_SearchResults getEntities(final String schema, final String subType, final String matchType,
			final String value, final int maxResults) {
		logger.debug("=========================================================In ${projectName} doing ${projectName} STUFF");
		G_SearchResults metaresults = null;
		if (ValidationUtils.isValid(value)) {
			if (ValidationUtils.isValid(matchType)) {
				try {
					graphene.model.idl.G_Constraint.valueOf(matchType);
				} catch (final Exception e) {
				}
			} else {
			}

			final G_PropertyMatchDescriptor identifiers = G_PropertyMatchDescriptor.newBuilder().setKey("_all")
					.setListRange(new ListRangeHelper(G_PropertyType.STRING, value))
					.setConstraint(graphene.model.idl.G_Constraint.EQUALS).build();

			try {
				final QueryHelper sq = new QueryHelper(identifiers);// queryBuilder.build();
				sq.setTargetSchema(index);
				sq.setMaxResult(defaultMaxResults);
				if (isUserExists()) {
					sq.setUserId(getUser().getId());
					sq.setUsername(getUser().getUsername());
				}
				// if (ValidationUtils.isValid(subType) &&
				// !subType.contains(DataSourceListDAO.ALL_REPORTS)) {
				// final G_PropertyMatchDescriptor filters =
				// G_PropertyMatchDescriptor
				// .newBuilder()
				// .setKey("filters")
				// .setRange(
				// new ListRangeHelper(G_PropertyType.STRING,
				// graphene.util.StringUtils
				// .tokenizeToStringCollection(subType, ",")))
				// .setConstraint(graphene.model.idl.G_Constraint.EQUALS).build();
				// sq.getPropertyMatchDescriptors().add(filters);
				// }
				loggingDao.recordQuery(sq);
				// if (currentSelectedWorkspaceExists) {
				// List<G_EntityQuery> qo =
				// currentSelectedWorkspace.getQueryObjects();
				// if (qo == null) {
				// qo = new ArrayList<G_EntityQuery>(1);
				// }
				// qo.add(sq);
				// currentSelectedWorkspace.setQueryObjects(qo);
				//
				// userDataAccess.saveWorkspace(getUser().getId(),
				// currentSelectedWorkspace);
				// }
				metaresults = dao.search(sq);

			} catch (final Exception e) {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, e.getMessage());
				logger.error(e.getMessage());
			}
		}
		if ((metaresults == null) || (metaresults.getResults().size() == 0)) {
			alertManager.alert(Duration.TRANSIENT, Severity.INFO, "No results found for " + value + ".");
			resultShowingCount = 0;
			resultTotalCount = 0;
		} else {
			alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Showing " + metaresults.getResults().size()
					+ " of " + metaresults.getTotal() + " results found.");
		}
		return metaresults;
	}

	private G_Entity getEntity() {
		if (currentSearchResult != null) {
			return (G_Entity) currentSearchResult.getResult();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getExtLink() {
		return extPath + getMediaId();
	}

	public Collection<Tuple<String, String>> getHashTagsInCaption() {
		return (Collection<Tuple<String, String>>) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.HASHTAGS_IN_CAPTION));
	}

	public Collection<Tuple<String, String>> getHashTagsInComments() {
		return (Collection<Tuple<String, String>>) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.HASHTAGS_IN_COMMENTS));
	}

	/**
	 * Get a list of icons that apply to this report.
	 * 
	 * @return
	 */
	public Collection<Tuple<String, String>> getIconList() {
		// return (Collection<Tuple<String, String>>)
		// currentEntity.get(DocumentGraphParser.ICONLIST);
		return new ArrayList();
	}

	public Collection<Triple<String, String, String>> getIdentifierList() {
		// return (Collection<Triple<String, String, String>>)
		// currentEntity.get(DocumentGraphParser.SUBJECTIDLIST);
		return new ArrayList();
	}

	public String getMediaCaption() {
		return (String) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.MEDIA_CAPTION_TEXT));
	}

	public String getMediaCommentCount() {
		return String.valueOf(PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.MEDIA_COMMENT_COUNT)));
	}

	public Object getMediaCreatedTime() {
		return PropertyHelper.getSingletonValue(getEntity().getProperties().get(${projectName}Parser.MEDIA_CREATED_TIME));
	}

	public String getMediaId() {
		return (String) PropertyHelper.getSingletonValue(getEntity().getProperties().get(${projectName}Parser.MEDIA_ID));
	}

	public String getMediaLikeCount() {
		return String.valueOf(PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.MEDIA_LIKE_COUNT)));
	}

	public String getMediaLocationLatLon() {
		return (String) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.MEDIA_LOCATION_LATLON));
	}

	public String getMediaLocationName() {
		return (String) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.MEDIA_LOCATION_NAME));
	}

	public String getMediaOwner() {
		return (String) PropertyHelper.getSingletonValue(getEntity().getProperties().get(${projectName}Parser.MEDIA_OWNER));
	}

	public String getMediaPageLink() {
		return (String) PropertyHelper.getSingletonValue(getEntity().getProperties().get(${projectName}Parser.MEDIA_LINK));
	}

	public String getMediaThumbnail() {
		return (String) PropertyHelper.getSingletonValue(getEntity().getProperties().get(
				${projectName}Parser.MEDIA_THUMBNAIL));
	}

	protected Messages getMessages() {
		return messages;
	}

	public BeanModel getModel() {
		if (model == null) {
			model = beanModelSource.createEditModel(Object.class, messages);
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
		}

		return model;
	}

	public Format getMoneyFormat() {
		return DataFormatConstants.getMoneyFormat();
	}

	public Collection<Triple<String, String, String>> getNameList() {
		// return (Collection<Triple<String, String, String>>)
		// currentEntity.get(DocumentGraphParser.SUBJECTNAMELIST);
		return new ArrayList();
	}

	public Link getNamePivotLink(final String term) {
		// XXX: pick the right search type based on the link value
		final Link l = searchPage.set(null, null, G_Constraint.EQUALS.name(), term, defaultMaxResults);
		return l;
	}

	public JSONObject getOptions() {

		final JSONObject json = new JSONObject(
				"bJQueryUI",
				"true",
				"bAutoWidth",
				"true",
				"sDom",
				"<${symbol_escape}"col-sm-4${symbol_escape}"f><${symbol_escape}"col-sm-4${symbol_escape}"i><${symbol_escape}"col-sm-4${symbol_escape}"l><${symbol_escape}"row${symbol_escape}"<${symbol_escape}"col-sm-12${symbol_escape}"p><${symbol_escape}"col-sm-12${symbol_escape}"r>><${symbol_escape}"row${symbol_escape}"<${symbol_escape}"col-sm-12${symbol_escape}"t>><${symbol_escape}"row${symbol_escape}"<${symbol_escape}"col-sm-12${symbol_escape}"ip>>");
		// Sort by score then by date.
		json.put("aaSorting",
				new JSONArray().put(new JSONArray().put(0).put("asc")).put(new JSONArray().put(3).put("desc")));

		final JSONArray columnArray = new JSONArray();

		// a two-dimensional array that acts as a definition and mapping between
		// column headers and their widths (in %)
		final String[][] properties = { { "rank", "1%" }, { "actions", "10%" }, { "informationIcons", "8%" },
				{ "date", "7%" }, { "amount", "7%" }, { "subjects", "12%" }, { "addressList", "25%" },
				{ "communicationIdentifierList", "15%" }, { "identifierList", "15%" } };

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "sType", "numeric"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "sType", "string"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "false",
				"sWidth", properties[columnArray.length()][1], "sType", "html"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "sType", "date"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "type", "currency", "sType", "currency"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "sType", "string"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "sType", "string"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "sType", "string"));

		columnArray.put(new JSONObject("mDataProp", properties[columnArray.length()][0], "bSortable", "true", "sWidth",
				properties[columnArray.length()][1], "sType", "string"));

		// json.put("aoColumns", columnArray);
		json.put("oLanguage", new JSONObject("sSearch", "Filter:"));

		return json;
	}

	public Link getPivotLink(final String term) {
		final Link l = searchPage.set(null, null, G_Constraint.CONTAINS.name(), term, defaultMaxResults);
		return l;
	}

	public Long getRank() {
		return (Long) PropertyHelper.getSingletonValue(getEntity().getProperties().get(G_Parser.CARDINAL_ORDER));
	}

	public String getScore() {
		return DataFormatConstants.formatScore(
				(Double) PropertyHelper.getSingletonValue(getEntity().getProperties().get(G_Parser.SCORE)), 0.0d);
	}

	/**
	 * @return the searchValue
	 */
	public final String getSearchValue() {
		return searchValue;
	}

	public String getStyleFor(final G_Property p) {
		boolean containsTerm = false;
		// splits on " ", "/", and "${symbol_escape}" in the search term.
		// FIXME: tokenize based on " and use the substring between them as a
		// single term
		final String[] searchTerms = currentSearchValue.split("[ ${symbol_escape}${symbol_escape}/]+");
		for (final String term : searchTerms) {
			// term.replaceAll() looks for all " characters and removes them for
			// the sake of comparison
			if (StringUtils.containsIgnoreCase(p.getKey(), term.replaceAll("[${symbol_escape}"]+", ""))) {
				containsTerm = true;
				break;
			}
		}
		// FIXME: We need to be passing something like the canonical types so we
		// get the right colors.
		return style.getStyle(p.getFriendlyText(), containsTerm);
	}

	/**
	 * @return the user
	 */
	public final G_User getUser() {
		return user;
	}

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	/**
	 * @return the userExists
	 */
	public final boolean isUserExists() {
		return userExists;
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
				searchResults = getEntities(searchSchema, searchTypeFilter, searchMatch, currentSearchValue,
						(int) maxResults);

			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			searchResults = null;
		}

	}

	/**
	 * @param user
	 *            the user to set
	 */
	public final void setUser(final G_User user) {
		this.user = user;
	}

}
