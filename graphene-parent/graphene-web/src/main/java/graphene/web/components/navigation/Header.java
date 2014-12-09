package graphene.web.components.navigation;

import graphene.model.idl.G_SymbolConstants;
import graphene.util.validator.ValidationUtils;
import graphene.web.services.SearchBrokerService;

import java.util.Date;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.PageRenderLinkSource;

public class Header {
	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogoWideDark;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;
	@Inject
	@Path("context:/core/img/flags/us.png")
	@Property
	private Asset imgFlagUS;

	@Property
	private int numberTasks = 5;

	@Property
	private int numberNotifications = 2;
	@Property
	private int numberMessages = 3;

	@Inject
	private SearchBrokerService broker;
	@Persist
	@Property
	private String searchValue;

	public Date getCurrentTime() {
		return new Date();
	}

	@Inject
	private ComponentClassResolver componentClassResolver;
	@Inject
	private AlertManager alertManager;
	@Inject
	private PageRenderLinkSource prls;

	Object onSuccessFromGlobalSearchForm() {
		Object retval = null;
		if (ValidationUtils.isValid(searchValue)) {
			Link link = prls.createPageRenderLinkWithContext(
					broker.getSearchPage(searchValue), searchValue);
			retval = link;
		} else {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					"Please enter a valid search value.");
		}
		if (!ValidationUtils.isValid(retval)) {
			alertManager
					.alert(Duration.TRANSIENT, Severity.WARN,
							"There is no search broker configured for this instance of Graphene");
		}
		return retval;
	}

}
