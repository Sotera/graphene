package graphene.model.idlhelper;

import graphene.business.commons.exception.BusinessException;
import graphene.model.idl.AuthenticationException;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.tynamo.security.internal.services.LoginContextService;

/**
 * Basic security interface for synchonizing the login/logout functions with the
 * creation/destruction of related session state objects. Some implementations
 * may also provide authentication. Others will integrate with 3rd Party
 * authenticators, like Shiro Realms.
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
	 * Gets username of current user
	 */
	String getUsername();
	
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
	void login(String username, String password) throws AuthenticationException, AvroRemoteException, BusinessException;

	Object loginAndRedirect(String grapheneLogin, String graphenePassword, boolean grapheneRememberMe,
			RequestGlobals requestGlobals, LoginContextService loginContextService, Response response,
			Messages messages, AlertManager alertManager);

	boolean loginAuthenticatedUser(String username);

	/**
	 * Logs out the user
	 */
	@Log
	Object logout();
}
