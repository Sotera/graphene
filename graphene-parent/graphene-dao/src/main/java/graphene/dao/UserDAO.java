package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_User;

import java.util.List;

public interface UserDAO {

	/**
	 * Count the number of users whose username matches partialName
	 * 
	 * @param partialName
	 * @return the number of usernames that match
	 */
	public long countUsers(String partialName);

	/**
	 * Given a 'detached' User POJO, persist it however the implementation wants
	 * to: if the user object exists, update it. If the user object didn't
	 * already exist, create it. Returns the User object after it has been
	 * saved. Note that it may now have updated timestamps or new data as a
	 * result of it being persisted. (Which is why we give it back to you as a
	 * convenience, so you don't have to query for it.)
	 * 
	 * @param user
	 * @return
	 */
//	public G_User createOrUpdate(G_User user);

	/**
	 * Deletes the user found by username, and also deletes ALL relationships to
	 * the user.
	 * 
	 * @param username
	 */
	public boolean delete(String id);

	// Disable User by Username
	public boolean disable(String id);

	// Enable User by Username
	public boolean enable(String id);

	// Get All Users
	public List<G_User> getAllUsers();

	// Find and return a user, do not create
	public G_User getById(String id);

	public List<G_User> getByPartialUsername(String partialName, int offset,
			int limit);

	// Find and return a user, do not create
	public G_User getByUsername(String userName);

	/**
	 * 
	 * @param username
	 * @param password
	 * @return Return the hash of the password, or null if there was an error.
	 */
	public String getPasswordHash(String id, String password);

	public boolean isExistingId(String id);

	/**
	 * We need this kind of method for times where we are checking if the
	 * username is taken.
	 * 
	 * @param username
	 * @return
	 */
	public boolean isExisting(String username);

	public G_User loginUser(String id, String password)
			throws AuthenticationException;

	/**
	 * 
	 * @param person
	 * @return the user with any updates that the DAO might have done.
	 */
	public G_User save(G_User user);

	public boolean updatePasswordHash(String id, String passwordHash);

	public void initialize() throws DataAccessException;

	public G_User loginAuthenticatedUser(String id);

}
