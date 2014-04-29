package graphene.web.components.navigation;

import graphene.model.idl.G_User;
import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.func.Tuple;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.slf4j.Logger;

public class Menu {
	@Inject
	private AssetSource assetSource;
	@Inject
	private ComponentClassResolver componentClassResolver;
	@Inject
	private Locale locale;
	boolean onlyPluginPages = false;

	@Property
	private Tuple<String, String> page;

	@Property
	private String pageName;

	@Persist
	private Collection<Tuple<String, String>> pageNames;

	private boolean userExists;

	@Property
	@SessionState(create = false)
	private G_User user;

	public String getAvatar() {
		if (userExists) {
			return assetSource.getContextAsset(
					"core/img/avatars/" + user.getAvatar(), locale)
					.toClientURL();
		} else {
			return assetSource.getContextAsset("core/img/avatars/default.png",
					locale).toClientURL();
		}
	}

	@Inject
	private Logger logger;

	public Collection<Tuple<String, String>> getPageNames() {
		if (pageNames != null && pageNames.size() > 0) {
			return pageNames;
		} else {
			logger.info("Constructing page links for side menu items.");
			List<String> pageList = this.componentClassResolver.getPageNames();
			pageNames = new ArrayList<Tuple<String, String>>();
			for (final String pageName : pageList) {
				String className = this.componentClassResolver
						.resolvePageNameToClassName(pageName);
				Class clazz = loadClass(className);
				if (clazz.isAnnotationPresent(PluginPage.class)) {
					PluginPage p = (PluginPage) clazz
							.getAnnotation(PluginPage.class);

					List<G_VisualType> vtlist = Arrays.asList(p.visualType());
					if (!vtlist.contains(G_VisualType.HIDDEN)) {
						String icon = "fa fa-lg fa-fw fa-zoom-in";
						if (vtlist.contains(G_VisualType.GRAPH)) {
							icon = "fa fa-lg fa-fw fa-code-fork"; //TODO: Find a better graph icon?
						} else if (vtlist.contains(G_VisualType.CHART)) {
							icon = "fa fa-lg fa-fw fa-chart"; //having to do with graphs
						} else if (vtlist.contains(G_VisualType.LIST)) {
							icon = "fa fa-lg fa-fw fa-code-fork"; //having to do with a list of things
						} else if (vtlist.contains(G_VisualType.TEXT)) {
							icon = "fa fa-lg fa-fw fa-code-fork"; //having to do with text or documents
						} else if (vtlist.contains(G_VisualType.EVENT)) {
							icon = "fa fa-lg fa-fw fa-calendar"; //TODO: Find a better icon for events
						} else if (vtlist.contains(G_VisualType.MONEY)) {
							icon = "fa fa-lg fa-fw fa-usd";  //If the page has anything to do with money, like an invoice or stocks
						} else if (vtlist.contains(G_VisualType.SEARCH)) {
							icon = "fa fa-lg fa-fw fa-zoom-in";  //if the page has anything to do with launching a search
						} else if (vtlist.contains(G_VisualType.META)) {
							icon = "fa fa-lg fa-fw fa-question-sign"; //having to do with the state of the system
						} else {
							icon = "fa fa-lg fa-fw fa-code-fork";
						}
						// XXX: Map the library name to a variable so it matches
						// the
						// library name in the AppModule
						pageNames
								.add(new Tuple<String, String>(pageName, icon));
					}
				}
			}
			return pageNames;
		}
	}

	private Class loadClass(final String className) {
		try {
			return Thread.currentThread().getContextClassLoader()
					.loadClass(className);
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}