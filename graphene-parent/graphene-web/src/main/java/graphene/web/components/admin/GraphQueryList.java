package graphene.web.components.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_GraphViewEvent;
import graphene.web.components.BasicDataTable;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

public class GraphQueryList extends BasicDataTable {
	@Inject
	protected LoggingDAO loggingDao;
	@Property
	private G_GraphViewEvent current;

	@Persist
	private BeanModel<G_GraphViewEvent> model;
	@Property
	private List<G_GraphViewEvent> list;

	public BeanModel<G_GraphViewEvent> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(G_GraphViewEvent.class, resources.getMessages());
			model.exclude("userId", "id", "schema", "queryObject", "ReportPageLink");
			model.reorder("timeinitiated", "username", "reportid", "reporttype");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getGraphViewEvents(null, 0, 200000);
	}
}
