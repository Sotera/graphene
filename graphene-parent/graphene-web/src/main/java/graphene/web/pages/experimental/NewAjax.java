package graphene.web.pages.experimental;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import java.util.Date;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = "context:core/js/testJSON.js")
@PluginPage(visualType = G_VisualType.EXPERIMENTAL, menuName = "Ajax test", icon = "fa fa-lg fa-fw fa-cogs")
public class NewAjax {
	@Inject
	private ComponentResources resources;

	@Inject
	private Request request;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	public String getEventLink() {
		return resources.createEventLink("MyCustomEventName").toURI();
	}

	void afterRender() {
		javaScriptSupport.addScript("setupEvent('%s', '%s');", "linkId",
				getEventLink());
	}

	@OnEvent(value = "MyCustomEventName")
	JSONArray myEventHandler() {
		// check if this is a AJAX request
		if (request.isXHR()) {
			String queryParameter1 = request.getParameter("queryParameter1");
			String queryParameter2 = request.getParameter("queryParameter2");
			JSONArray object = new JSONArray();
			object.put(1, "\n" + queryParameter1.toUpperCase());
			object.put(2, "\n" + queryParameter2.toUpperCase());
			object.put(3, getMessage());
			// Make your real payload
			return object;
		}
		return null;
	}

	private String getMessage() {
		return "SUCCESS at " + (new Date()).toString();
	}
}
