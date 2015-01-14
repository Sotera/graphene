package graphene.web.pages;

import graphene.dao.CombinedDAO;
import graphene.dao.DataSourceListDAO;
import graphene.dao.DocumentGraphParser;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_VisualType;
import graphene.model.query.EntityQuery;
import graphene.model.view.GrapheneResults;
import graphene.services.HyperGraphBuilder;
import graphene.util.DataFormatConstants;
import graphene.util.ExceptionUtil;
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
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
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
@PluginPage(visualType = G_VisualType.SEARCH, menuName = "Combined Search", icon = "fa fa-lg fa-fw fa-search")
@Import(stylesheet = {
		"context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:/core/js/libs/jquery/jquery-ui-1.10.3.min.js")
public class CombinedEntitySearchPage extends SimpleBasePage {

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Inject
	private BeanModelSource beanModelSource;

	@Property
	private String currentAddress;
	@Property
	private String currentCommunicationId;
	@Property
	private String currentDate;
	@Property
	private Map<String, Object> currentEntity;

	@Property
	private String currentIcon;

	@Property
	private String currentIdentifier;

	@Property
	private String currentName;

	@Inject
	private CombinedDAO dao;
	@Inject
	private DataSourceListDAO dataSourceListDAO;

	@Inject
	private JavaScriptSupport javaScriptSupport;
	@Inject
	@Symbol(G_SymbolConstants.EXT_PATH)
	private String extPath;
	@Property
	private final GridDataSource gds = new CombinedEntityDataSource(dao);

	@Property
	@Persist
	private boolean highlightZoneUpdates;

	@InjectComponent
	private Zone listZone;

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

	@Inject
	private Request request;

	@Property
	private GrapheneResults<Object> results;
	@Property
	private int resultShowingCount;

	@Property
	private int resultTotalCount;

	@Property
	private String searchType;

	private String searchValue;

	@Property
	private Object selectedEvent;

	public Collection<String> getAddressList() {
		return (Collection<String>) currentEntity
				.get(DocumentGraphParser.SUBJECTADDRESSLIST);
	}

	public Long getAmount() {
		return (Long) currentEntity.get(DocumentGraphParser.TOTALAMOUNTNBR);
	}

	public Format getMoneyFormat() {
	    Locale locale = new Locale("en", "US"); 
		return NumberFormat.getCurrencyInstance(locale);
	}
	
	//public String getAmount() {
	//	Long l = (Long) currentEntity.get(DocumentGraphParser.TOTALAMOUNTNBR);
	//	return DataFormatConstants.formatMoney(l);
	//}
	
	public Collection<String> getCIdentifierList() {
		return (Collection<String>) currentEntity
				.get(DocumentGraphParser.SUBJECTCIDLIST);
	}

	public Format getDateFormat() {
		return new SimpleDateFormat(getDatePattern());
	}

	public String getDatePattern() {
		return DataFormatConstants.DATE_FORMAT_STRING;
	}

	private GrapheneResults<Object> getEntities(final String type,
			final String value) {
		GrapheneResults<Object> metaresults = null;
		if (ValidationUtils.isValid(value)) {
			final EntityQuery sq = new EntityQuery();
			sq.addAttribute(new G_SearchTuple<String>(value,
					G_SearchType.COMPARE_CONTAINS));
			sq.setMaxResult(200);
			sq.setSchema(type);
			sq.setMinimumScore(0.50);
			if (isUserExists()) {
				sq.setUserId(getUser().getId());
				sq.setUserName(getUser().getUsername());
			}

			try {
				loggingDao.recordQuery(sq);
				metaresults = dao.findByQueryWithMeta(sq);
				final TimeReporter tr = new TimeReporter(
						"parsing details of results", logger);
				populatedTableResults = new ArrayList<Map<String, Object>>();
				// Populate all the results!
				for (final Object m : metaresults.getResults()) {
					final DocumentGraphParser parserForObject = phgb
							.getParserForObject(m);
					parserForObject.populateExtraFields(m, sq);
					populatedTableResults.add(parserForObject
							.getAdditionalProperties(m));
				}
				tr.logAsCompleted();
			} catch (final Exception e) {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
						e.getMessage());
				e.printStackTrace();
			}
		}
		if ((metaresults == null)
				|| (metaresults.getNumberOfResultsReturned() == 0)) {
			alertManager.alert(Duration.TRANSIENT, Severity.INFO,
					"No results found for " + value + ".");
			resultShowingCount = 0;
			resultTotalCount = 0;
		} else {
			alertManager
					.alert(Duration.TRANSIENT, Severity.SUCCESS, "Showing "
							+ metaresults.getNumberOfResultsReturned() + " of "
							+ metaresults.getNumberOtResultsTotal()
							+ " results found.");
		}
		return metaresults;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getExtLink() {
		return extPath + getReportId();
	}

	/**
	 * Get a list of icons that apply to this report.
	 * 
	 * @return
	 */
	public Collection<String> getIconList() {

		return (Collection<String>) currentEntity.get(DocumentGraphParser.ICONLIST);
	}

	public Collection<String> getIdentifierList() {
		return (Collection<String>) currentEntity.get(DocumentGraphParser.SUBJECTIDLIST);
	}

	public BeanModel getModel() {
		final BeanModel<Object> model = beanModelSource.createEditModel(Object.class, messages);

		model.addEmpty("actions");
		model.addEmpty("score");
		model.addEmpty("informationIcons");
		model.addEmpty("date");
		model.addEmpty("amount");
		model.addEmpty("subjects");
		model.addEmpty("addressList");
		model.addEmpty("communicationIdentifierList");
		model.addEmpty("identifierList");

		model.getById("score").sortable(true);
		model.getById("amount").sortable(true);
		model.getById("date").sortable(true);
		model.getById("actions").sortable(true);
		model.getById("subjects").sortable(true);
		model.getById("informationIcons").sortable(true);

		return model;
	}

	public Collection<String> getNameList() {
		return (Collection<String>) currentEntity.get(DocumentGraphParser.SUBJECTNAMELIST);

	}

	// /////////////////////////////////////////////////////////////////////
	// FILTER
	// /////////////////////////////////////////////////////////////////////

	public JSONObject getOptions() {

		final JSONObject json = new JSONObject(
				"bJQueryUI",
				"true",
				"sDom",
				"<\"col-sm-4\"f><\"col-sm-4\"i><\"col-sm-4\"l><\"row\"<\"col-sm-12\"p><\"col-sm-12\"r>><\"row\"<\"col-sm-12\"t>><\"row\"<\"col-sm-12\"ip>>");
		// Sort by score then by date.
		json.put(
				"aaSorting",
				new JSONArray().put(new JSONArray().put(1).put("desc")).put(
						new JSONArray().put(3).put("desc")));

		return json;
	}

	public String getReportId() {
		return (String) currentEntity.get(DocumentGraphParser.REPORT_ID);
	}

	public String getReportPageLink() {
		return (String) currentEntity.get(DocumentGraphParser.REPORT_LINK);
	}

	public String getReportType() {
		return (String) currentEntity.get(DocumentGraphParser.REPORT_TYPE);
	}

	public String getScore() {
		final Double d = (Double) currentEntity.get(DocumentGraphParser.SCORE);
		return DataFormatConstants.formatScore(d);
	}

	/**
	 * @return the searchValue
	 */
	public final String getSearchValue() {
		return searchValue;
	}

	public String getStyleForAddress() {
		if (StringUtils.containsIgnoreCase(currentAddress, searchValue)) {
			return "bg-color-red txt-color-white";
		} else {
			return "bg-color-yellow txt-color-white";
		}
	}

	public String getStyleForIdentifier() {
		if (StringUtils.containsIgnoreCase(currentIdentifier, searchValue)) {
			return "bg-color-red txt-color-white";
		} else {
			return "bg-color-magenta txt-color-white";
		}
	}

	public String getStyleForName() {
		if (StringUtils.containsIgnoreCase(currentName, searchValue)) {
			return "bg-color-red txt-color-white";
		} else {
			return "bg-color-orange txt-color-white";
		}
	}

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	@Log
	void onActivate(final String searchValue) {
		this.searchValue = searchValue;
	}

	@Log
	String onPassivate() {
		if (ValidationUtils.isValid(searchValue)) {
			return searchValue;
		} else {
			logger.debug("Passivating with no values");
			return null;
		}
	}

	void onSuccessFromFilterForm() {
		if (ValidationUtils.isValid(searchValue)) {
			if ((results == null)
					|| (results.getNumberOfResultsReturned() == 0)
					|| !previousSearchValue.equalsIgnoreCase(searchValue)) {
				// don't use cached version.
				try {
					results = getEntities(searchType, searchValue);
				} catch (final Exception ex) {
					// record error to screen!
					final String message = ExceptionUtil
							.getRootCauseMessage(ex);
					alertManager.alert(Duration.SINGLE, Severity.ERROR,
							"ERROR: " + message);
					logger.error(message);
				}
				previousSearchValue = searchValue;
			}
			if (request.isXHR()) {
				logger.debug("Rendering AJAX response");
				ajaxResponseRenderer.addRender(listZone);
			}
		}
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
	void setupRender() {
		if (ValidationUtils.isValid(searchValue)) {
			try {
				results = getEntities(searchType, searchValue);

			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			results = new GrapheneResults<Object>();
		}

	}

}
