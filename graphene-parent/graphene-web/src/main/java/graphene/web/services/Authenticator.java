package graphene.web.services;

import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_User;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.annotations.Log;


/**
 * Basic security interface
 * 
 * @author karesti
 */
public interface Authenticator
{

    /**
     * Gets the logged user
     * 
     * @return User, the logged User
     */
    G_User getLoggedUser();

    /**
     * Checks if the current user is logged in
     * 
     * @return true if the user is logged in
     */
    boolean isLoggedIn();

    /**
     * Logs the user.
     * 
     * @param username
     * @param password
     * @throws AuthenticationException
     *             throw if an error occurs
     * @throws AvroRemoteException 
     */
    @Log
    void login(String username, String password) throws AuthenticationException, AvroRemoteException;

    /**
     * Logs out the user
     */
    @Log
    void logout();
}
