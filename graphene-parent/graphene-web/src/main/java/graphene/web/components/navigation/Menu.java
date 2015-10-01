package graphene.web.components.navigation;

import graphene.dao.GroupDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserRoleDAO;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.util.Triple;
import graphene.web.annotations.PluginPage;
import graphene.web.model.MenuItem;
import graphene.web.model.MenuItemTreeModelAdapter;
import graphene.web.model.MenuType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Tree;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.tree.DefaultTreeModel;
import org.apache.tapestry5.tree.TreeModel;
import org.slf4j.Logger;

public class Menu {
	@Inject
	private AssetSource assetSource;
	@Inject
	private ComponentClassResolver componentClassResolver;
	@Inject
	private G_UserDataAccess userDataAccess;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_ADMIN)
	@Property
	private boolean enableAdmin;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_EXPERIMENTAL)
	@Property
	private boolean enableExperimental;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_MISC)
	@Property
	private boolean enableMisc;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_SETTINGS)
	@Property
	private boolean enableSettings;

	@Inject
	private Locale locale;
	@Inject
	private Logger logger;
	boolean onlyPluginPages = false;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_WORKSPACES)
	protected boolean workspacesEnabled;
	@Property
	private Triple<String, String, String> page;

	@Property
	private String pageName;

	@Persist
	private Map<MenuType, Collection<Triple<String, String, String>>> menuHierarchy;

	@Property
	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Inject
	private UserGroupDAO ugDao;

	@Inject
	private GroupDAO gDao;
	@Inject
	private UserRoleDAO urDao;

	private static final MenuItem rootNode = new MenuItem("home", "Home", "index");

	// @InjectComponent
	private Tree tree;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private MenuItem menuItem;

	@Inject
	@Symbol(G_SymbolConstants.EXTERNAL_ADMIN_ROLE_NAME)
	private String externalAdminRoleName;

	@Inject
	private RequestGlobals rq;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_MANAGED_SECURITY)
	protected boolean applicationManagedSecurity;

	public Collection<Triple<String, String, String>> getActionPages() {
		return menuHierarchy.get(MenuType.ACTION);
	}

	public Collection<Triple<String, String, String>> getAdminPages() {
		return menuHierarchy.get(MenuType.ADMIN);
	}

	public String getAvatar() {
		if (userExists) {
			return assetSource.getContextAsset("core/img/avatars/" + user.getAvatar(), locale).toClientURL();
		} else {
			logger.warn("User object does not exist.");
			return assetSource.getContextAsset("core/img/avatars/default.png", locale).toClientURL();
		}
	}

	public Collection<Triple<String, String, String>> getExperimentalPages() {
		return menuHierarchy.get(MenuType.EXPERIMENTAL);
	}

	public Collection<Triple<String, String, String>> getLastActionPages() {
		return menuHierarchy.get(MenuType.LASTACTION);
	}

	public TreeModel<MenuItem> getMenuItemModel() {
		final ValueEncoder<MenuItem> encoder = new ValueEncoder<MenuItem>() {

			@Override
			public String toClient(final MenuItem value) {
				// TODO Auto-generated method stub
				return value.uuid;
			}

			@Override
			public MenuItem toValue(final String clientValue) {
				// TODO Auto-generated method stub
				return rootNode.seek(clientValue);
			}

		};
		return new DefaultTreeModel<MenuItem>(encoder, new MenuItemTreeModelAdapter(), rootNode.getChildren());

	}

	public Collection<Triple<String, String, String>> getMetaPages() {
		return menuHierarchy.get(MenuType.META);
	}

	public Collection<Triple<String, String, String>> getSettingsPages() {
		return menuHierarchy.get(MenuType.SETTINGS);
	}

	@SetupRender
	void initializeValue() {

		if (menuHierarchy == null) {
			logger.info("Constructing page links for side menu items.");
			final List<String> pageList = componentClassResolver.getPageNames();

			menuHierarchy = new HashMap<MenuType, Collection<Triple<String, String, String>>>();
			menuHierarchy.put(MenuType.ACTION, new ArrayList<Triple<String, String, String>>(1));
			menuHierarchy.put(MenuType.LASTACTION, new ArrayList<Triple<String, String, String>>(1));
			menuHierarchy.put(MenuType.META, new ArrayList<Triple<String, String, String>>(1));
			menuHierarchy.put(MenuType.EXPERIMENTAL, new ArrayList<Triple<String, String, String>>(1));
			menuHierarchy.put(MenuType.ADMIN, new ArrayList<Triple<String, String, String>>(1));
			menuHierarchy.put(MenuType.SETTINGS, new ArrayList<Triple<String, String, String>>(1));

			// TODO: Work this out. I think this is a better solution to the
			// problem of a dynamic, pluggable, contributable menu tree.
			rootNode.addChild(new MenuItem("", "Action", ""));

			for (final String pageName : pageList) {
				final String className = componentClassResolver.resolvePageNameToClassName(pageName);

				final Class clazz = loadClass(className);
				if (clazz.isAnnotationPresent(PluginPage.class)) {
					final PluginPage p = (PluginPage) clazz.getAnnotation(PluginPage.class);

					final List<G_VisualType> vtlist = Arrays.asList(p.visualType());
					if (!vtlist.contains(G_VisualType.HIDDEN)) {
						if (vtlist.contains(G_VisualType.GRAPH) || vtlist.contains(G_VisualType.TOP)
								|| vtlist.contains(G_VisualType.GEO) || vtlist.contains(G_VisualType.TEXT)
								|| vtlist.contains(G_VisualType.EVENT) || vtlist.contains(G_VisualType.SEARCH)
								|| vtlist.contains(G_VisualType.MONEY) || vtlist.contains(G_VisualType.LIST)) {
							menuHierarchy.get(MenuType.ACTION).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));

						}
						if (workspacesEnabled && vtlist.contains(G_VisualType.VIEW_WORKSPACE)) {
							menuHierarchy.get(MenuType.ACTION).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));
						} else if (workspacesEnabled && vtlist.contains(G_VisualType.MANAGE_WORKSPACES)) {
							menuHierarchy.get(MenuType.ACTION).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));
						} else if (vtlist.contains(G_VisualType.HELP)) {
							menuHierarchy.get(MenuType.LASTACTION).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));

						} else if (vtlist.contains(G_VisualType.META)) {
							menuHierarchy.get(MenuType.META).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));
						} else if (applicationManagedSecurity && vtlist.contains(G_VisualType.SETTINGS)) {
							menuHierarchy.get(MenuType.SETTINGS).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));
						} else if (vtlist.contains(G_VisualType.ADMIN)) {
							menuHierarchy.get(MenuType.ADMIN).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));

						} else if (vtlist.contains(G_VisualType.EXPERIMENTAL)) {
							menuHierarchy.get(MenuType.EXPERIMENTAL).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));
						} else {
							menuHierarchy.get(MenuType.EXPERIMENTAL).add(
									new Triple<String, String, String>(pageName, p.icon(), p.menuName()));
						}

					}
				}
			}

		}
	}

	private Class loadClass(final String className) {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}