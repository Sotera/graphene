package graphene.model.idl;

/**
 * Modeled after org.apache.tapestry5.SymbolConstants Note for version we use
 * SymbolConstants.APPLICATION_VERSION because it will also adjust caching and
 * asset versioning rules for us.
 * 
 * @author djue
 * 
 */
public class G_SymbolConstants {
	public static final String APPLICATION_NAME = "graphene.application-name";

	// The string for who to contact about this application
	public static final String APPLICATION_CONTACT = "graphene.application-contact";
	public static final String APPLICATION_VERSION = "graphene.application-version";
	public static final String THEME_PATH = "graphene.theme-path";
	public static final String DATABASE_PROPERTIES_LOCATION = "graphene.database-properties-location";
	public static final String MIDTIER_SERVER_URL = "graphene.midtier-database-url";

	public static final String MIDTIER_SERVER_USERNAME = "graphene.midtier-database-username";
	public static final String MIDTIER_SERVER_PASSWORD = "graphene.midtier-database-password";
	public static final String MIDTIER_SERVER2_URL = "graphene.midtier2-database-url";

	public static final String MIDTIER_SERVER2_USERNAME = "graphene.midtier2-database-username";
	public static final String MIDTIER_SERVER2_PASSWORD = "graphene.midtier2-database-password";
	/**
	 * Should not be overriden by runtime properties. The value of this key is
	 * supplied by the core's app module, and it is hard coded into all the
	 * core's components and T5 pages at this time.
	 */
	public static final String GRAPHENE_WEB_CORE_PREFIX = "graphene.core-web-prefix";

	/**
	 * Menu view settings
	 */
	public static final String ENABLE_EXPERIMENTAL = "graphene.enable-experimental";

	public static final String ENABLE_MISC = "graphene.enable-misc";
	public static final String ENABLE_ADMIN = "graphene.admin-misc";
	public static final String ENABLE_SETTINGS = "graphene.settings-misc";
	/**
	 * Report View Settings
	 */
	public static final String ENABLE_TAG_CLOUDS = "graphene.enable-tag-clouds";

	/**
	 * Something like "/graphene-customer-web/graph.html?&entity="
	 */
	public static final String EXT_PATH = "graphene.ext-path";

	public static final String DEFAULT_MAX_SEARCH_RESULTS = "graphene.default-max-search-results";

	public static final String ENABLE_FEDERATED_LOGIN = "graphene.enable-federated-login";

	public static final String ENABLE_FREE_TEXT_EXTRACTION = "graphene.enable-free-text-extraction";

	public static final String ENABLE_GRAPH_QUERY_PATH = "graphene.enable-graph-query-path";
	public static final String ENABLE_WORKSPACES = "graphene.enable-workspaces";
	public static final String ENABLE_DELETE_WORKSPACES = "graphene.enable-delete-workspaces";
	public static final String ENABLE_DELETE_UNUSED_WORKSPACES = "graphene.enable-delete-unused-workspaces";
	public static final String ENABLE_DELETE_USERS = "graphene.enable-delete-users";

	public static final String ENABLE_DELETE_GROUPS = "graphene.enable-delete-groups";

	public static final String ENABLE_DELETE_LOGS = "graphene.enable-delete-logs";

	public static final String ENABLE_DELETE_DATASOURCES = "graphene.enable-delete-datasources";

	public static final String ENABLE_DELETE_USER_GROUP = "graphene.enable-delete-user-groups";

	public static final String ENABLE_DELETE_USER_WORKSPACES = "graphene.enable-delete-user-workspaces";

	public static final String ENABLE_DELETE_ROLES = "graphene.enable-delete-roles";

	public static final String ENABLE_DELETE_USER_ROLE = "graphene.enable-delete-user-roles";
	public static final String DEFAULT_ADMIN_ACCOUNT = "graphene.default-admin-account";
	public static final String DEFAULT_ADMIN_PASSWORD = "graphene.default-admin-password";
	public static final String DEFAULT_ADMIN_EMAIL = "graphene.default-admin-email";

	public static final String DEFAULT_GRAPH_TRAVERSAL_DEGREE = "graphene.default-graph-traversal-degree";

	public static final String DEFAULT_MAX_GRAPH_NODES = "graphene.default-max-graph-nodes";

	public static final String DEFAULT_MAX_GRAPH_EDGES_PER_NODE = "graphene.default-max-graph-edges-per-node";
	public static final String DEFAULT_MIN_SCORE = "graphene.default-min-score";
	public static final String DEFAULT_ADMIN_GROUP_NAME = "graphene.default-admin-group-name";
	/**
	 * This is the role a user from an LDAP would have, which corresponds to the
	 * admininstrator role.
	 */
	public static final String EXTERNAL_ADMIN_ROLE_NAME = "graphene.external-admin-role-name";
	public static final String EXTERNAL_USER_ROLE_NAME = "graphene.external-user-role-name";
	public static final String ENABLE_AUTO_LOGIN = "graphene.enable-auto-login";
	public static final String AUTO_LOGIN_USERNAME = "graphene.auto-login-name";

	public static final String ENABLE_LOGGING = "graphene.enable-logging";
	public static final String REQUIRE_AUTHENTICATION = "graphene.require-authentication";

	/**
	 * Input validation symbols
	 */
	public static final String USER_PASSWORD_VALIDATION = "graphene.user-password-validation";
	public static final String USER_PASSWORD_VALIDATION_MESSAGE = "graphene.user-password-validation-message";

	public static final String USER_NAME_VALIDATION = "graphene.user-name-validation";
	public static final String USER_NAME_VALIDATION_MESSAGE = "graphene.user-name-validation-message";

	public static final String WORKSPACE_NAME_VALIDATION = "graphene.workspace-name-validation";
	public static final String WORKSPACE_NAME_VALIDATION_MESSAGE = "graphene.workspace-name-validation-message";

	public static final String APPLICATION_MANAGED_SECURITY = "graphene.application-managed-security";

	public static final String INHERIT_NODE_ATTRIBUTES = "graphene.inherit-node-attributes";

	private G_SymbolConstants() {
		// TODO Auto-generated constructor stub
	}
}
