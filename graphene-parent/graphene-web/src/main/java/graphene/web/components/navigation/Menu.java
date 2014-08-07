package graphene.web.components.navigation;

import graphene.model.idl.G_User;
import graphene.model.idl.G_VisualType;
import graphene.util.Triple;
import graphene.web.annotations.PluginPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.slf4j.Logger;

public class Menu {
	private static final String EXPERIMENTAL = "experimental";
	private static final String ACTION = "action";
	private static final String META = "meta";
	@Inject
	private AssetSource assetSource;
	@Inject
	private ComponentClassResolver componentClassResolver;
	@Inject
	private Locale locale;
	boolean onlyPluginPages = false;

	@Property
	private Triple<String, String, String> page;

	@Property
	private String pageName;

	@Persist
	private Map<String, Collection<Triple<String, String, String>>> pageNames;

	private boolean userExists;

	public Collection<Triple<String, String, String>> getActionPages() {
		return pageNames.get(ACTION);
	}

	public Collection<Triple<String, String, String>> getExperimentalPages() {
		return pageNames.get(EXPERIMENTAL);
	}

	public Collection<Triple<String, String, String>> getMetaPages() {
		return pageNames.get(META);
	}

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

	@SetupRender
	void initializeValue() {
		if (pageNames == null) {
			logger.info("Constructing page links for side menu items.");
			List<String> pageList = this.componentClassResolver.getPageNames();
			pageNames = new HashMap<String, Collection<Triple<String, String, String>>>();
			pageNames.put("action",
					new ArrayList<Triple<String, String, String>>(1));
			pageNames.put("meta",
					new ArrayList<Triple<String, String, String>>(1));
			pageNames.put("experimental",
					new ArrayList<Triple<String, String, String>>(1));
			for (final String pageName : pageList) {
				String className = this.componentClassResolver
						.resolvePageNameToClassName(pageName);
				Class clazz = loadClass(className);
				if (clazz.isAnnotationPresent(PluginPage.class)) {
					PluginPage p = (PluginPage) clazz
							.getAnnotation(PluginPage.class);

					List<G_VisualType> vtlist = Arrays.asList(p.visualType());
					if (!vtlist.contains(G_VisualType.HIDDEN)) {
						if (vtlist.contains(G_VisualType.GRAPH)
								|| vtlist.contains(G_VisualType.TOP)
								|| vtlist.contains(G_VisualType.GEO)
								|| vtlist.contains(G_VisualType.TEXT)
								|| vtlist.contains(G_VisualType.EVENT)
								|| vtlist.contains(G_VisualType.SEARCH)
								|| vtlist.contains(G_VisualType.MONEY)
								|| vtlist.contains(G_VisualType.LIST)) {
							pageNames.get(ACTION).add(
									new Triple<String, String, String>(
											pageName, p.icon(), p.menuName()));

						} else if (vtlist.contains(G_VisualType.META)) {
							pageNames.get(META).add(
									new Triple<String, String, String>(
											pageName, p.icon(), p.menuName()));
						} else if (vtlist.contains(G_VisualType.EXPERIMENTAL)) {
							pageNames.get(EXPERIMENTAL).add(
									new Triple<String, String, String>(
											pageName, p.icon(), p.menuName()));
						} else {
							pageNames.get(EXPERIMENTAL).add(
									new Triple<String, String, String>(
											pageName, p.icon(), p.menuName()));
						}

					}
				}
			}

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