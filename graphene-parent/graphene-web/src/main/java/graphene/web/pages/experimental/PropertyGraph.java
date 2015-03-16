package graphene.web.pages.experimental;

import graphene.dao.HyperGraphBuilder;
import graphene.model.idl.G_VisualType;
import graphene.util.validator.ValidationUtils;
import graphene.web.annotations.PluginPage;
import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GraphQuery;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.slf4j.Logger;

@Import(stack = { "CytoscapeStack" })
@PluginPage(visualType = G_VisualType.EXPERIMENTAL, menuName = "Property Graph", icon = "fa fa-lg fa-fw fa-code-fork")
public class PropertyGraph {
	@Property
	private String theProperty;

	@Property
	@Persist
	private boolean highlightZoneUpdates;

	@InjectService("HyperProperty")
	private HyperGraphBuilder propertyGraphBuilder;

	@Inject
	private Logger logger;

	@Inject
	private AlertManager alertManager;

	@Property
	@Persist
	private V_CSGraph graph;

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	void onActivate(final String theProperty) {
		this.theProperty = theProperty;
	}

	String onPassivate() {
		return theProperty;
	}

	void setupRender() {
		if (ValidationUtils.isValid(theProperty)) {
			try {
				V_GenericGraph g = null;
				graph = null;
				try {
					final V_GraphQuery q = new V_GraphQuery();
					q.addSearchIds(theProperty);
					q.setDirected(false);
					q.setMaxNodes(300);
					q.setMaxEdgesPerNode(20);
					q.setMaxHops(5);
					g = propertyGraphBuilder.makeGraphResponse(q);
					graph = new V_CSGraph(g, true);
				} catch (final Exception e) {
					logger.error(e.getMessage());
				}
			} catch (final Exception e1) {
				alertManager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: " + e1.getMessage());
				logger.debug(e1.getMessage());
			}
		} else {
			graph = null;
		}
	}
}
