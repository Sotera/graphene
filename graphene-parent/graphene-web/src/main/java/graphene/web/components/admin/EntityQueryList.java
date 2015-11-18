package graphene.web.components.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idlhelper.RangeHelper;
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
	private G_EntityQuery current;

	@Property
	private G_PropertyMatchDescriptor currentPmd;

	@Persist
	private BeanModel<G_EntityQuery> model;

	@Property
	private List<G_EntityQuery> list;

	public String getCurrentRange() {
		return RangeHelper.toString(currentPmd);
	}

	public BeanModel<G_EntityQuery> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(G_EntityQuery.class, resources.getMessages());
			model.exclude("caseSensitive", "searchFreeText", "initiatorId", "attributevalues", "minimumscore",
					"minsecs", "maxsecs", "sortcolumn", "sortfield", "firstresult", "maxresult", "datasource",
					"userId", "sortascending", "id", "schema");

			model.get("propertyMatchDescriptors").sortable(true);
			model.reorder("timeinitiated", "username", "propertyMatchDescriptors");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getQueries(null, null, 0, 200000);
	}
}
