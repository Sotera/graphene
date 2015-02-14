package graphene.web.components.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_ReportViewEvent;
import graphene.web.components.BasicDataTable;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ReportViewList extends BasicDataTable {
	@Inject
	protected LoggingDAO loggingDao;
	@Property
	private G_ReportViewEvent current;
	@Persist
	private BeanModel<G_ReportViewEvent> model;
	@Property
	private List<G_ReportViewEvent> list;

	public BeanModel<G_ReportViewEvent> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(G_ReportViewEvent.class, resources.getMessages());
			model.exclude("schema", "userId", "id");
			model.reorder("timeinitiated", "username", "reportId", "reporttype", "reportpagelink");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getReportViewEvents(null, 0, 200000);
	}
}
