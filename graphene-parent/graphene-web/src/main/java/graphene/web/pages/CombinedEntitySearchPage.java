package graphene.web.pages;

import graphene.dao.CombinedDAO;
import graphene.dao.DataSourceListDAO;
import graphene.dao.ReportPopulator;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_VisualType;
import graphene.model.query.EntityQuery;
import graphene.model.query.SearchCriteria;
import graphene.model.view.GrapheneResults;
import graphene.util.DataFormatConstants;
import graphene.util.ExceptionUtil;
import graphene.util.validator.ValidationUtils;
import graphene.web.annotations.PluginPage;
import graphene.web.model.CombinedEntityDataSource;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
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
public class CombinedEntitySearchPage extends SimpleBasePage {

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Inject
	private AlertManager alertManager;

	@Inject
	private BeanModelSource beanModelSource;

	@SessionState
	@Property
	private SearchCriteria criteria;

	@Property
	private String currentAddress;
	@Property
	private String currentIcon;
	@Property
	private String currentDate;

	@Property
	private String currentCommunicationId;

	@Property
	private Object currentEntity;

	@Property
	private String currentIdentifier;

	@Property
	private String currentName;

	@Inject
	private CombinedDAO dao;

	@Inject
	private DataSourceListDAO dataSourceListDAO;

	@Property
	private Object drillDownevent;

	private String drillDownId;

	// /////////////////////////////////////////////////////////////////////
	// FILTER
	// /////////////////////////////////////////////////////////////////////

	@Property
	private GrapheneResults<Object> results;

	@Property
	private GridDataSource gds = new CombinedEntityDataSource(dao);

	@Property
	@Persist
	private boolean highlightZoneUpdates;

	@InjectComponent
	private Zone listZone;

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	private String previousSearchValue;

	@Inject
	private Request request;

	@Property
	private String searchType;

	@Property
	private int resultShowingCount;
	@Property
	private int resultTotalCount;

	/**
	 * @return the searchValue
	 */
	public final String getSearchValue() {
		return searchValue;
	}

	/**
	 * @param searchValue
	 *            the searchValue to set
	 */
	public final void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	private String searchValue;

	@Property
	private Object selectedEvent;

	@Inject
	private ReportPopulator<Object, String> reportPopulator;

	@Inject
	@Symbol(SymbolConstants.APPLICATION_FOLDER)
	private String path;

	@Inject
	@Symbol(G_SymbolConstants.EXT_PATH)
	private String extPath;

	/**
	 * 
	 * 
	 * @return
	 */
	public String getExtLink() {
		return extPath + getReportId();
	}

	public Set<String> getReportDates() {
		return reportPopulator.getDates(currentEntity);
	}

	public String getReportAmount() {
		return reportPopulator.getAmount(currentEntity);
	}

	/**
	 * Get a list of icons that apply to this report.
	 * 
	 * @return
	 */
	public Collection<String> getIconList() {
		return reportPopulator.getIcons(currentEntity, searchValue);
	}

	public Collection<String> getAddressList() {
		return reportPopulator.getAddresses(currentEntity);
	}

	public Collection<String> getCIdentifierList() {
		return reportPopulator.getCIdentifiers(currentEntity);
	}

	public Format getDateFormat() {
		return new SimpleDateFormat(getDatePattern());
	}

	public String getDatePattern() {
		return DataFormatConstants.DATE_FORMAT_STRING;
	}



	private GrapheneResults<Object> getEntities(String type, String value) {
		GrapheneResults<Object> metaresults = null;
		if (ValidationUtils.isValid(value)) {
			EntityQuery sq = new EntityQuery();
			sq.addAttribute(new G_SearchTuple<String>(value,
					G_SearchType.COMPARE_CONTAINS));
			sq.setMaxResult(200);
			sq.setSchema(type);
			if(isUserExists()){
				sq.setUserId(getUser().getId());
				sq.setUserName(getUser().getUsername());
			}
			
			try {
				loggingDao.recordQuery(sq);
				metaresults = dao.findByQueryWithMeta(sq);

			} catch (Exception e) {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
						e.getMessage());
				e.printStackTrace();
			}
		}
		if (metaresults == null
				|| metaresults.getNumberOfResultsReturned() == 0) {
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

	public Collection<String> getIdentifierList() {
		return reportPopulator.getIdentifiers(currentEntity);
	}

	public BeanModel getModel() {
		BeanModel<Object> model = beanModelSource.createEditModel(Object.class, messages);
		model.add("actions", null);
		model.add("reportSummary", null);
		model.add("amount", null);
		model.add("nameList", null);
		model.add("addressList", null);
		model.add("communicationIdentifierList", null);
		model.add("identifierList", null);
		return model;
	}

	public Collection<String> getNameList() {
		return reportPopulator.getNames(currentEntity);
	}

	public String getReportId() {
		return reportPopulator.getReportId(currentEntity);
	}

	public String getReportPageLink() {
		return reportPopulator.getPageLink(currentEntity);
	}

	public String getReportType() {
		return reportPopulator.getReportType(currentEntity);
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
	void onActivate(String searchValue) {
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
			if (results == null || results.getNumberOfResultsReturned() == 0
					|| !previousSearchValue.equalsIgnoreCase(searchValue)) {
				// don't use cached version.
				try {
					results = getEntities(searchType, searchValue);
				} catch (Exception ex) {
					// record error to screen!
					String message = ExceptionUtil.getRootCauseMessage(ex);
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
	 * This should help with persisted values.
	 */
	void setupRender() {
		if (ValidationUtils.isValid(searchValue)) {
			try {
				results = getEntities(searchType, searchValue);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			results = new GrapheneResults<Object>();
		}

	}

}
