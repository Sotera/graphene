package graphene.web.components.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_UserLoginEvent;
import graphene.web.components.BasicDataTable;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

public class UserLoginList extends BasicDataTable {
	@Inject
	protected LoggingDAO loggingDao;
	@Property
	private G_UserLoginEvent current;

	@Persist
	private BeanModel<G_UserLoginEvent> model;
	@Property
	private List<G_UserLoginEvent> list;

	public BeanModel<G_UserLoginEvent> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(G_UserLoginEvent.class, resources.getMessages());
			model.exclude("id", "schema", "userid");
			model.reorder("timeinitiated", "username");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getUserLoginEvents(null, 0, 200000);
	}
}
