/**
 * Use this module when you have your Authentication and Authorization being done by an external source, such as a Tomcat hooked up to an LDAP, etc.
 * 
 * The underlying assumption is that in this scenario, no user can even reach our application until they have been Authenticated and Authorized by the container.  Thus, our application doesn't need to worry about passwords, etc.
 * 
 * Since we don't manage user accounts' registration, etc, we auto-create a new user account if it doesn't exist.
 * 
 * We also expect the username to be the 'user principal' or 'remoteUser' specified in each request header.
 * 
 * @author djue
 *
 */
package graphene.security.tomcat.preaa;