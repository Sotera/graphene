package graphene.web.pages;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_VisualType;
import graphene.services.LinkGenerator;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.got5.tapestry5.jquery.ImportJQueryUI;
import org.slf4j.Logger;

/**
 * This will be interesting now that we're using G_Properties. We may need
 * special rendering blocks that take the properties and render them based on
 * the internals of the properties
 * 
 * For instance, if the range is a single value and a String, display like a
 * string.
 * 
 * If the range is something with a beginning and end, render it differently
 * based on the type (like a date range or value range)
 * 
 * Also, we can exploit any other information in the property, like provenance,
 * uncertainty and other tags.
 * 
 * @author djue
 * 
 */
@PluginPage(visualType = G_VisualType.HIDDEN, menuName = "Combined Search", icon = "fa fa-lg fa-fw fa-search")
@Import(stylesheet = { "context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:/core/js/libs/jquery/jquery-ui-1.10.3.min.js")
public class CombinedEntitySearchPage extends SimpleBasePage implements LinkGenerator {

	@Inject
	private Logger logger;

	/**
	 * The overall schema, such as an ES index
	 */
	@ActivationRequestParameter(value = "schema")
	@Property
	private String searchSchema;
	/**
	 * Aka subschema aka the type a user would usually select between, such as
	 * an ES type
	 */
	@ActivationRequestParameter(value = "type")
	@Property
	@Persist
	private String searchTypeFilter;

	@ActivationRequestParameter(value = "maxResults")
	@Property
	private long maxResults;
	/**
	 * The value the user types in
	 */
	@ActivationRequestParameter(value = "term")
	@Property
	private String searchValue;

	/**
	 * Using a default persist caused the value to not be overwritten when
	 * performing a new search.
	 */
	@Persist(PersistenceConstants.FLASH)
	@Property
	private G_EntityQuery prebuiltQuery;

	@InjectPage
	private CombinedEntitySearchPage searchPage;

	/**
	 * The type of query to run
	 */
	@ActivationRequestParameter(value = "match")
	@Property
	private String searchMatch;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Override
	public Link set(final String schema, final String type, final String match, final String value,
			final long maxResults) {
		searchSchema = schema;
		searchTypeFilter = type;
		searchValue = value;
		searchMatch = match;
		this.maxResults = maxResults;
		// Try resetting any persisted value of this type.
		prebuiltQuery = null;
		return pageRenderLinkSource.createPageRenderLink(this.getClass());
	}

	public void setPrebuilt(final G_EntityQuery q) {
		searchSchema = null;
		searchTypeFilter = null;
		searchValue = null;
		searchMatch = null;
		prebuiltQuery = q;
	}

	/**
	 * This should help with persisted values.
	 */

	void setupRender() {
		logger.debug("In Combined search, prebuilt is " + prebuiltQuery);
	}
}
