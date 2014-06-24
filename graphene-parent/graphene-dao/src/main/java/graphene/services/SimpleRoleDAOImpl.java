/**
 * 
 */
package graphene.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import graphene.dao.RoleDAO;
import graphene.model.idl.G_Role;

/**
 * @author djue
 * 
 */
public class SimpleRoleDAOImpl implements RoleDAO {

	private static final G_Role ADMIN = new G_Role("admin",
			"Administrator Role");
	private static final G_Role USER = new G_Role("user", "User Role");

	@Inject
	private Logger logger;
public SimpleRoleDAOImpl() {
	// TODO Auto-generated constructor stub
}
	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.dao.RoleDAO#getForUsername(java.lang.String)
	 */
	@Override
	public List<G_Role> getForUsername(String username) {
		// TODO Auto-generated method stub
		ArrayList<G_Role> list = new ArrayList<G_Role>();
		list.add(ADMIN);
		list.add(USER);
		logger.debug("Returning roles: " + list);
		return list;
	}

}
