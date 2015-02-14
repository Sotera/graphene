package graphene.web.components.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_SearchTuple;
import graphene.model.query.EntityQuery;
import graphene.web.components.BasicDataTable;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EntityQueryList extends BasicDataTable {
	@Inject
	protected LoggingDAO loggingDao;
	@Property
	private EntityQuery current;
	@Property
	private String currentFilter;
	@Property
	private List<String> currentFilters;
	@Property
	private G_SearchTuple<String> currentTuple;

	@Persist
	private BeanModel<EntityQuery> model;
	@Property
	private List<EntityQuery> list;

	public BeanModel<EntityQuery> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(EntityQuery.class, resources.getMessages());
			model.exclude("caseSensitive", "searchFreeText", "initiatorId", "attributevalues", "minimumscore",
					"minsecs", "maxsecs", "sortcolumn", "sortfield", "firstresult", "maxresult", "datasource",
					"userId", "sortascending", "id", "schema");

			model.get("AttributeList").sortable(true);
			model.reorder("timeinitiated", "username", "AttributeList");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getQueries(null, null, 0, 200000);
	}
}
