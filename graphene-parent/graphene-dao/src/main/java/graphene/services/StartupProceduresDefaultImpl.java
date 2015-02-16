package graphene.services;

import graphene.dao.GroupDAO;
import graphene.dao.PermissionDAO;
import graphene.dao.RoleDAO;
import graphene.dao.StartupProcedures;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.util.validator.ValidationUtils;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.DateTime;
import org.slf4j.Logger;

public class StartupProceduresDefaultImpl implements StartupProcedures {
	@Inject
	private G_UserDataAccess userDataAccess;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_ADMIN_ACCOUNT)
	private String defaultAdminUsername;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_ADMIN_EMAIL)
	private String defaultAdminEmail;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_ADMIN_PASSWORD)
	private String defaultAdminPassword;
	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_ADMIN_GROUP_NAME)
	private String defaultAdminGroupname;

	@Inject
	private GroupDAO gDao;
	@Inject
	private PermissionDAO pDao;
	@Inject
	private RoleDAO rDao;
	@Inject
	private UserDAO uDao;
	@Inject
	private UserGroupDAO ugDao;
	@Inject
	private UserWorkspaceDAO uwDao;

	@Inject
	private WorkspaceDAO wDao;
	@Inject
	private Logger logger;

	@Override
	public boolean initialize() {
		boolean success = false;
		try {
			final G_Role adminRole = rDao.create(new G_Role(null, "admin", "Administrator Role", DateTime.now()
					.getMillis()));
			rDao.create(new G_Role(null, "user", "Standard User Role", DateTime.now().getMillis()));

			gDao.createGroup(new G_Group(null, defaultAdminGroupname, "A group for administrators", DateTime.now()
					.getMillis()));
			G_User admin = userDataAccess.getByUsername(defaultAdminUsername);
			if (!ValidationUtils.isValid(admin)) {
				logger.debug("Creating an admin user with the default admin user name " + defaultAdminUsername);
				admin = new G_User();
				admin.setFullname(defaultAdminUsername);
				admin.setEmail(defaultAdminEmail);
				admin.setUsername(defaultAdminUsername);
				admin.setId(null);
				admin = userDataAccess.registerUser(admin, defaultAdminPassword, false);
			}
			success = userDataAccess.addRole(admin, adminRole);
		} catch (final AvroRemoteException e) {
			e.printStackTrace();
		}
		return success;
	}
}
