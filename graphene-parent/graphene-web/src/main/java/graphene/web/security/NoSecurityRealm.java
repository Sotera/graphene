package graphene.web.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
	private G_UserDataAccess userDao;
	@Inject
	private Logger logger;
	private static String REALM_NAME = "grapheneRealm";

	public NoSecurityRealm() {
		setName(REALM_NAME);
		setCredentialsMatcher(new CredentialsMatcher() {
			private PasswordHash hasher = new PasswordHash();

			@Override
			public boolean doCredentialsMatch(AuthenticationToken token,
					AuthenticationInfo info) {
				return true;
			}
		});
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		String username = (String) principals.fromRealm(getName()).iterator()
				.next();
		logger.debug("Getting authorization for " + username);
		if (username == null) {
			throw new AccountException(
					"Null usernames are not allowed by this realm.");
		}
		G_User user = null;
		try {
			user = userDao.getUser(username);
		} catch (AvroRemoteException e1) {
			e1.printStackTrace();
		}
		SimpleAuthorizationInfo info = null;
		if (user != null) {
			try {
				info = new SimpleAuthorizationInfo();
				for (G_Role role : userDao.getRolesByUsername(user
						.getUsername())) {
					info.addRole(role.getDescription());

					for (G_Permission permission : userDao
							.getPermissionsByRole(role)) {
						info.addStringPermission(permission.getDescription());
					}
				}
			} catch (AvroRemoteException e) {
				e.printStackTrace();
			}
		}
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authToken;
		logger.debug("Getting authentication for " + token);
		G_User user = null;
		try {
			user = userDao.getUser(token.getUsername());
		} catch (AvroRemoteException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		if (user != null) {
			// We are putting the previously stored hashed password in here.
			return new SimpleAuthenticationInfo(user.getUsername(),
					user.getHashedpassword(), getName());
		} else {
			return null;
		}
	}
}
