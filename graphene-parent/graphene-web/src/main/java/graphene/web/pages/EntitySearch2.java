package graphene.web.pages;

import graphene.dao.DataSourceListDAO;
import graphene.dao.EntityDAO;
import graphene.model.datasourcedescriptors.DataSet;
import graphene.model.datasourcedescriptors.DataSource;
import graphene.model.datasourcedescriptors.DataSourceList;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_VisualType;
import graphene.model.query.AdvancedSearch2;
import graphene.model.query.SearchFilter;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.slf4j.Logger;

/**
 * This type of search page is a test UI to imitate/replicate/replace the Ext
 * based entity search screen.
 * 
 * It may also serve as a template for other search screens where we want a
 * dynamic description of criteria, to construct the next-gen search query
 * objects
 * 
 * ToDo:
 * 
 * Ability to save searches as part of the workspace. This would be very useful
 * for large queries that contain a lot of identifiers or uploaded files as
 * search criteria. long term persistence in a workspace or between workspaces
 * would allow sharing of queries between analysts, subscriptions (upload search
 * to the cloud and have live data run across them in a Storm/Esper like
 * fashion) Another benefit is enhanced auditing and logging of analyst actions
 * for oversight.
 * 
 * 
 * 
 * @author djue
 * 
 */
@PluginPage(visualType = G_VisualType.SEARCH)
public class EntitySearch2 {
	@Property
	@Persist
	private AdvancedSearch2 query;
	@Inject
	private DataSourceListDAO dataSourceListDAO;

	@Inject
	private EntityDAO entitydao;
	@Property
	private SearchFilter option;

	@Property
	private Boolean deleted;

	@OnEvent(EventConstants.ACTIVATE)
	void init() {
		if (query == null)
			query = new AdvancedSearch2();
	}

	@Property
	private SelectModel datasourceSelectModel;
	@Property
	private SelectModel fieldSelectModel;
	@Inject
	SelectModelFactory selectModelFactory;
	@SuppressWarnings("unused")
	private DataSource currentDatasource;
	private DataSet currentDataset;

	void setupRender() {
		DataSourceList dsl = dataSourceListDAO.getList();

		datasourceSelectModel = selectModelFactory.create(dsl.getDataSources(),
				"friendlyName");
		fieldSelectModel = selectModelFactory.create(currentDataset.getFields(),
				"friendlyName");
	}

	public ValueEncoder<SearchFilter> getSearchFilterEncoder() {
		return new ValueEncoder<SearchFilter>() {
			public String toClient(SearchFilter value) {
				return value.getValue();
			}

			public SearchFilter toValue(String clientValue) {
				for (SearchFilter currentSearchFilter : query.getOptions()) {
					if (currentSearchFilter.getValue() != null
							&& clientValue.equals(currentSearchFilter
									.getValue()))
						return currentSearchFilter;
				}

				return null;
			}
		};
	}

	@Inject
	private Logger logger;

	@OnEvent(EventConstants.SUCCESS)
	public void onSuccess() {
		logger.debug(query.toString());
	}

	@OnEvent(value = EventConstants.ADD_ROW, component = "options")
	public Object onAddRowFromSearchFilters() {
		SearchFilter option = new SearchFilter();
		option.setCompareType(G_SearchType.COMPARE_CONTAINS);
		option.setValue("");

		query.add(option);

		return option;
	}

	@OnEvent(value = EventConstants.REMOVE_ROW, component = "options")
	void onRemoveRowFromSearchFilters(SearchFilter option) {
		query.remove(option);
	}
}