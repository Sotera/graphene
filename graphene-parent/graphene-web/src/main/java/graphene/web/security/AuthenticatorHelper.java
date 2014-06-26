package graphene.web.security;

import graphene.model.idl.AuthenticationException;
import graphene.web.model.BusinessException;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.annotations.Log;

/**
 * Basic security interface for synchonizing the login/logout functions with the
 * creation/destruction of related session state objects. Some implementations
 * may also provide authentication.  Others will integrate with 3rd Party authenticators, like Shiro Realms.
 * 
 * @author karesti, djue
 */
public interface AuthenticatorHelper {

	/**
	 * Gets the logged user
	 * 
	 * @return User, the logged User
	 */
	// G_User getLoggedUser();

	/**
	 * Checks if the current user is logged in
	 * 
	 * @return true if the user is logged in
	 */
	boolean isUserObjectCreated();

	/**
	 * Logs the user in.
	 * 
	 * @param username
	 * @param password
	 * @throws AuthenticationException
	 *             throw if an error occurs
	 * @throws AvroRemoteException
	 * @throws BusinessException
	 */
	@Log
	void login(String username, String password)
			throws AuthenticationException, AvroRemoteException,
			BusinessException;

	/**
	 * Logs out the user
	 */
	@Log
	void logout();
}
