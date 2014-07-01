package graphene.web.pages;

import graphene.dao.EntityGraphDAO;
import graphene.model.idl.G_VisualType;
import graphene.model.query.EntityGraphQuery;
import graphene.util.ExceptionUtil;
import graphene.web.annotations.PluginPage;

import java.util.Date;
import java.util.List;

import mil.darpa.vande.converters.infovis.InfoVisGraphAdjacency;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Import(stylesheet = { "context:core/js/infovis/css/base.css",
		"context:core/js/infovis/css/ForceDirected.css" }, library = {
		"context:core/js/infovis/js/T5AjaxBinding.js",
		"context:core/js/infovis/js/jit-yc.js",
		"context:core/js/infovis/js/jit-fd3.js" })
@PluginPage(visualType = G_VisualType.EXPERIMENTAL, menuName = "Property Graph", icon = "fa fa-lg fa-fw fa-code-fork")
public class AttributeGraph {

	@Inject
	private EntityGraphDAO<InfoVisGraphAdjacency, EntityGraphQuery> dao;

	@Inject
	private Logger logger;
	@InjectComponent
	private Zone formZone;
	@Property
	@Persist
	private boolean highlightZoneUpdates;
	@Inject
	private JavaScriptSupport javaScriptSupport;
	@Inject
	private AlertManager manager;
	@Property
	@Persist
	private String partialName;
	@Inject
	private Request request;

	@Inject
	private ComponentResources resources;

	void afterRender() {
		javaScriptSupport.addScript("setupEvent('%s', '%s');", "linkId",
				getEventLink());
	}

	public String getEventLink() {
		return resources.createEventLink("MyCustomEventName").toURI();
	}

	private String getMessage() {
		return "SUCCESS at " + (new Date()).toString();
	}

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	@Log
	JSONArray onSuccessFromFilterForm() {
		// check if this is a AJAX request
		if (request.isXHR()) {
			String queryParameter1 = request.getParameter("queryParameter1");
			String queryParameter2 = request.getParameter("queryParameter2");

			JSONArray j = loadFromService();
			// JSONArray j = loadJSON2();
			if (j != null) {
				return j;
			} else {
				JSONArray jar = new JSONArray();
				try {
					jar.put(0, new JSONObject("Error", "bad json"));
				} catch (Exception ex) {
					String message = ExceptionUtil.getRootCauseMessage(ex);
					manager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: "
							+ message);
					logger.error(message);
				}
			}
		}
		return null;
	}

	@Log
	JSONArray loadFromService() {
		ObjectWriter ow = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		EntityGraphQuery q = new EntityGraphQuery();
		q.setAttribute(partialName);

		JSONArray j = null;
		try {
			List<InfoVisGraphAdjacency> o = dao.findByQuery(q);
			j = new JSONArray(ow.writeValueAsString(o));
			logger.debug("Found results: " + j);
		} catch (Exception ex) {
			String message = ExceptionUtil.getRootCauseMessage(ex);
			manager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: " + message);
			logger.error(message);
		}

		return j;
	}

	@Log
	@OnEvent(value = "MyCustomEventName")
	JSONArray myEventHandler() {
		// check if this is a AJAX request
		if (request.isXHR()) {
			String queryParameter1 = request.getParameter("queryParameter1");
			String queryParameter2 = request.getParameter("queryParameter2");
			partialName = "smith";
			JSONArray j = loadFromService();
			if (j != null) {
				return j;
			} else {
				JSONArray jar = new JSONArray();
				try {
					jar.put(0, new JSONObject("Error", "bad json"));
				} catch (Exception ex) {

					String message = ExceptionUtil.getRootCauseMessage(ex);
					manager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: "
							+ message);
					logger.error(message);
				}
			}
		}
		return null;
	}
}
