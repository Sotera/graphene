package graphene.web.security;

import graphene.model.idl.G_Permission;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.util.crypto.PasswordHash;

import org.apache.avro.AvroRemoteException;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * A Realm is Shiro terminology for a specialized DAO type object that knows how
 * to deal with Users, Roles, and Permissions. In online examples this is
 * sometimes done by called a different DAO for each of those object types.
 * 
 * With Graphene, we already had a similar object called G_UserDataAccess, which
 * talks to the appropriate DAOs. (G_UserDataAccess also does much more, and
 * deals with workspaces and user groups, etc)
 * 
 * @author djue
 * 
 */
public class NoSecurityRealm extends AuthorizingRealm {
	@Inject
	private G_UserDataAccess userDataAccess;
	@Inject
	private Logger logger;
	private static String REALM_NAME = "grapheneRealm";

	public NoSecurityRealm() {
		setName(REALM_NAME);
		setCredentialsMatcher(new CredentialsMatcher() {
			private final PasswordHash hasher = new PasswordHash();

			@Override
			public boolean doCredentialsMatch(final AuthenticationToken token, final AuthenticationInfo info) {
				return true;
			}
		});
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authToken)
			throws AuthenticationException {
		final UsernamePasswordToken token = (UsernamePasswordToken) authToken;
		logger.debug("Getting authentication for " + token);
		G_User user = null;
		try {
			user = userDataAccess.getByUsername(token.getUsername());
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		if (user != null) {
			// We are putting the previously stored hashed password in here.
			return new SimpleAuthenticationInfo(user.getUsername(), user.getHashedpassword(), getName());
		} else {
			return null;
		}
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
		final String username = (String) principals.fromRealm(getName()).iterator().next();
		logger.debug("Getting authorization for " + username);
		if (username == null) {
			throw new AccountException("Null usernames are not allowed by this realm.");
		}
		G_User user = null;
		try {
			user = userDataAccess.getByUsername(username);
		} catch (final AvroRemoteException e1) {
			e1.printStackTrace();
		}
		SimpleAuthorizationInfo info = null;
		if (user != null) {
			try {
				info = new SimpleAuthorizationInfo();
				for (final G_Role role : userDataAccess.getRolesByUser(user.getId())) {
					info.addRole(role.getName());
					logger.debug("User has role " + role.getName());
					for (final G_Permission permission : userDataAccess.getPermissionsByRole(role)) {
						info.addStringPermission(permission.getName());
						logger.debug("Role has permission " + permission.getName());
					}
				}
			} catch (final AvroRemoteException e) {
				logger.error(e.getMessage());
			}
		}
		return info;
	}
}
