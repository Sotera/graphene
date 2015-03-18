package graphene.web.pages;

import graphene.model.idl.G_VisualType;
import graphene.services.LinkGenerator;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
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

	// @Property
	// private String currentDate;
	// @Property
	// private G_SearchResult currentSearchResult;
	//
	// @Property
	// private Tuple<String, String> currentIcon;

	// @Property
	// private Triple<String, String, String> currentIdentifier;
	//
	// @Property
	// private Triple<String, String, String> currentName;
	//
	// @Property
	// private Tuple<String, String> currentAt;
	//
	// @Property
	// private Tuple<String, String> currentHashTag;
	//
	// @Inject
	// private CombinedDAO dao;
	//
	// @Inject
	// private JavaScriptSupport javaScriptSupport;

	@Inject
	private Logger logger;
	//
	// @Inject
	// private Messages messages;

	// @Inject
	// @Symbol(SymbolConstants.APPLICATION_FOLDER)
	// private String path;

	/**
	 * The outer list is a list of rows, the inner list is a list of columns.
	 * i.e. List[Row] and a Row is a List[Column]
	 */
	// @Property
	// private List<List<G_Property>> populatedTableResults;

	// @Property
	// private int resultShowingCount;

	// @Property
	// private G_SearchResults searchResults;

	// @Property
	// private int resultTotalCount;
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
	@Property
	private String searchValue;

	@InjectPage
	private CombinedEntitySearchPage searchPage;

	// @Inject
	// @Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	// private Integer defaultMaxResults;
	/**
	 * The type of query to run
	 */
	@ActivationRequestParameter(value = "match")
	@Property
	private String searchMatch;

	// @Property
	// private Object selectedEvent;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	// @Inject
	// @Symbol(G_SymbolConstants.EXT_PATH)
	// private String extPath;

	// @Property
	// @SessionState(create = false)
	// private G_Workspace currentSelectedWorkspace;

	// @Property
	// private boolean currentSelectedWorkspaceExists;
	// @Property
	// private String currentSearchValue;

	// @Inject
	// private StyleService style;

	// @Persist
	// private BeanModel<Object> model;
	//
	// final NumberFormat formatter = NumberFormat.getCurrencyInstance();

	// @Inject
	// private DocumentBuilder b;

	// public Collection<Triple<String, String, String>> getAddressList() {
	// return (Collection<Triple<String, String, String>>)
	// currentRow.get(DocumentGraphParser.SUBJECTADDRESSLIST);
	// }

	// public Link getAddressPivotLink(final String term) {
	// // XXX: pick the right search type based on the link value
	// final Link l = searchPage.set(null, null,
	// G_Constraint.COMPARE_EQUALS.name(), term, defaultMaxResults);
	// return l;
	// }

	// public Double getAmount() {
	// return (Double) currentRow.get(DocumentGraphParser.TOTALAMOUNTNBR);
	// }

	// public Collection<Tuple<String, String>> getAtsInCaption() {
	// return (Collection<Tuple<String, String>>)
	// currentRow.get("ATS_IN_CAPTION");
	// }

	// public Collection<Tuple<String, String>> getAtsInComments() {
	// return (Collection<Tuple<String, String>>)
	// currentRow.get("ATS_IN_COMMENTS");
	// }

	// public Collection<Triple<String, String, String>> getCIdentifierList() {
	// return (Collection<Triple<String, String, String>>)
	// currentRow.get(DocumentGraphParser.SUBJECTCIDLIST);
	// }

	// @Inject
	// @Property
	// private Block searchresultsview;

	/**
	 * Get a list of icons that apply to this report.
	 * 
	 * @return
	 */
	// public Collection<Tuple<String, String>> getIconList() {
	// return (Collection<Tuple<String, String>>)
	// currentRow.get(DocumentGraphParser.ICONLIST);
	// }

	// /////////////////////////////////////////////////////////////////////
	// FILTER
	// /////////////////////////////////////////////////////////////////////

	// public Collection<Triple<String, String, String>> getIdentifierList() {
	// return (Collection<Triple<String, String, String>>)
	// currentRow.get(DocumentGraphParser.SUBJECTIDLIST);
	// }
	// <t:jquery.datatable t:id="resultsdt"
	// t:source="searchResults.results" t:model="model"
	// t:row="currentSearchResult" t:options="options"
	// class="table table-striped table-bordered table-hover table-responsive"
	// t:rowsPerPage="20" t:empty="block:emptyResults">
	//
	//
	// </t:jquery.datatable>
	// @Inject
	// private Dynamic dynamic;

	// public Format getDateFormat() {
	// return new SimpleDateFormat(getDatePattern());
	// }

	// public String getDatePattern() {
	// return DataFormatConstants.DATE_FORMAT_STRING;
	// }

	/**
	 * 
	 * 
	 * @return
	 */
	// public String getExtLink() {
	// return extPath + getReportId();
	// }

	// public String getFormattedAmount() {
	// String amount = null;
	// try {
	// // amount = DataFormatConstants.formatMoney(getAmount());
	//
	// // amount = formatter.format(getAmount());
	// // DataFormatConstants.formatter.setParseIntegerOnly(true);
	// amount = DataFormatConstants.formatter.format(getAmount());
	//
	// // XXX: Hack to remove cents and decimal
	// amount = amount.subSequence(0, amount.length() - 3).toString();
	//
	// return amount;
	// } catch (final Exception e) {
	// e.printStackTrace();
	// }
	// return amount;
	// }

	/**
	 * 
	 * @param type
	 * @param value
	 * @param searchValue2
	 * @return
	 */
	// private G_SearchResults getEntities(final String schema, final String
	// subType, final String matchType,
	// final String value, final int maxResults) {
	// G_SearchResults metaresults = null;
	// if (ValidationUtils.isValid(value)) {
	// G_Constraint g_SearchType = null;
	// if (ValidationUtils.isValid(matchType)) {
	// try {
	// g_SearchType = G_Constraint.valueOf(matchType);
	// } catch (final Exception e) {
	// g_SearchType = G_Constraint.COMPARE_CONTAINS;
	// }
	// } else {
	// g_SearchType = G_Constraint.COMPARE_CONTAINS;
	// }
	// if (isUserExists()) {
	// getUser().getId();
	// getUser().getUsername();
	// }
	// final List<G_PropertyMatchDescriptor> tuples = new ArrayList<G_PropertyMatchDescriptor>();
	// final G_PropertyMatchDescriptor tuple = new G_PropertyMatchDescriptor<String>(value,
	// g_SearchType);
	// tuples.add(tuple);
	//
	// final List<String> filters = new ArrayList<String>();
	// if (ValidationUtils.isValid(subType) &&
	// !subType.contains(DataSourceListDAO.ALL_REPORTS)) {
	// filters.addAll(graphene.util.StringUtils.tokenizeToStringCollection(subType,
	// ","));
	// }
	//
	// final G_EntityQuery.Builder queryBuilder =
	// G_EntityQuery.newBuilder().setPropertyMatchDescriptors(tuples)
	// .setFilters(filters).setMaxResult(maxResults).setTargetSchema(schema).setMinimumScore(0.0)
	// .setTimeInitiated(DateTime.now().getMillis());
	//
	// if (isUserExists()) {
	// queryBuilder.setUserId(getUser().getId()).setUsername(getUser().getUsername());
	// }
	//
	// final G_EntityQuery sq = queryBuilder.build();
	//
	// try {
	// // loggingDao.recordQuery(sq);
	// // if (currentSelectedWorkspaceExists) {
	// // List<G_EntityQuery> qo =
	// // currentSelectedWorkspace.getQueryObjects();
	// // if (qo == null) {
	// // qo = new ArrayList<G_EntityQuery>(1);
	// // }
	// // qo.add(sq);
	// // currentSelectedWorkspace.setQueryObjects(qo);
	// //
	// // userDataAccess.saveWorkspace(getUser().getId(),
	// // currentSelectedWorkspace);
	// // }
	// metaresults = dao.findByQueryWithMeta(sq);
	// // final TimeReporter tr = new
	// // TimeReporter("parsing details of results", logger);
	// // populatedTableResults = new ArrayList<List<G_Property>>();
	// //
	// // // Populate all the results!
	// // for (final G_SearchResult m : metaresults.getResults()) {
	// // // final DocumentGraphParser parserForObject =
	// // // b.getParserForObject(m.getResult());
	// // // if (parserForObject != null) {
	// //
	// populatedTableResults.add(m.getNamedProperties().get(G_Parser.ROWFORTABLE));
	// // // } else {
	// // // logger.error("Could not find parser for " + m);
	// // // }
	// // }
	// // tr.logAsCompleted();
	// } catch (final Exception e) {
	// alertManager.alert(Duration.TRANSIENT, Severity.ERROR, e.getMessage());
	// logger.error(e.getMessage());
	// }
	// }
	// if ((metaresults == null) || (metaresults.getResults().size() == 0)) {
	// alertManager.alert(Duration.TRANSIENT, Severity.INFO,
	// "No results found for " + value + ".");
	// resultShowingCount = 0;
	// resultTotalCount = 0;
	// } else {
	// alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Showing " +
	// metaresults.getResults().size()
	// + " of " + metaresults.getTotal() + " results found.");
	// }
	// return metaresults;
	// }

	// public BeanModel getModel() {
	//
	// if (model == null) {
	// model = b.getModel(beanModelSource, messages);
	// }
	// model = beanModelSource.createEditModel(Object.class, messages);
	// model.addEmpty("rank");
	// model.addEmpty("actions");
	// model.addEmpty("informationIcons");
	// model.addEmpty("date");
	// model.addEmpty("amount");
	// model.addEmpty("subjects");
	// model.addEmpty("addressList");
	// model.addEmpty("communicationIdentifierList");
	// model.addEmpty("identifierList");
	//
	// model.getById("rank").sortable(true);
	// model.getById("actions").sortable(true);
	// model.getById("informationIcons").sortable(false);
	// model.getById("date").sortable(true);
	// model.getById("amount").sortable(true);
	// model.getById("subjects").sortable(true);
	// model.getById("addressList").sortable(true);
	// model.getById("communicationIdentifierList").sortable(true);
	// model.getById("identifierList").sortable(true);
	// }
	// return model;
	// }

	// public Format getMoneyFormat() {
	// return DataFormatConstants.getMoneyFormat();
	// }

	// public Collection<Triple<String, String, String>> getNameList() {
	// return (Collection<Triple<String, String, String>>)
	// currentRow.get(DocumentGraphParser.SUBJECTNAMELIST);
	//
	// }

	// public Link getNamePivotLink(final String term) {
	// // XXX: pick the right search type based on the link value
	// final Link l = searchPage.set(null, "media",
	// G_Constraint.COMPARE_EQUALS.name(), term, defaultMaxResults);
	// return l;
	// }

	// public JSONObject getOptions() {
	// return b.getOptions();
	// }
	//
	// public Link getPivotLink(final String term) {
	// final Link l = searchPage.set(null, null,
	// G_Constraint.COMPARE_CONTAINS.name(), term, defaultMaxResults);
	// return l;
	// }

	// public int getRank() {
	// return (int) currentRow.get(DocumentGraphParser.CARDINAL_ORDER);
	// }

	// public String getReportId() {
	// return (String) currentRow.get(DocumentGraphParser.REPORT_ID);
	// }

	// public String getReportPageLink() {
	// return (String) currentRow.get(DocumentGraphParser.REPORT_LINK);
	// }

	// public String getReportType() {
	// return (String) currentRow.get(DocumentGraphParser.REPORT_TYPE);
	// }

	// public String getScore() {
	// final Double d = (Double) currentRow.get(DocumentGraphParser.SCORE);
	// return DataFormatConstants.formatScore(d);
	// }

	/**
	 * @return the searchValue
	 */
	// public final String getSearchValue() {
	// return searchValue;
	// }

	// public String getStyleFor(final Triple<String, String, String>
	// currentThing) {
	// boolean containsTerm = false;
	// // splits on " ", "/", and "\" in the search term.
	// // FIXME: tokenize based on " and use the substring between them as a
	// // single term
	// final String[] searchTerms = currentSearchValue.split("[ \\/]+");
	// for (final String term : searchTerms) {
	// // term.replaceAll() looks for all " characters and removes them for
	// // the sake of comparison
	// if (StringUtils.containsIgnoreCase(currentThing.getThird(),
	// term.replaceAll("[\"]+", ""))) {
	// containsTerm = true;
	// break;
	// }
	// }
	// return style.getStyle(currentThing.getFirst(), containsTerm);
	// }

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

	//
	// /**
	// * @param searchValue
	// * the searchValue to set
	// */
	// public final void setSearchValue(final String searchValue) {
	// this.searchValue = searchValue;
	// }

	/**
	 * This should help with persisted values.
	 */
	// @Log
	void setupRender() {
		// if (ValidationUtils.isValid(searchValue)) {
		// currentSearchValue = searchValue;
		// try {
		// searchResults = getEntities(searchSchema, searchTypeFilter,
		// searchMatch, currentSearchValue,
		// (int) maxResults);
		//
		// } catch (final Exception e) {
		// logger.error(e.getMessage());
		// }
		// } else {
		// searchResults = null;
		// }

	}
}
