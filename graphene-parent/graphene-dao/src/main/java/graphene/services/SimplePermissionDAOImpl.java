/**
 * 
 */
package graphene.services;

import graphene.dao.PermissionDAO;
import graphene.model.idl.G_Permission;
import graphene.model.idl.G_Role;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * @author djue
 * 
 */
public class SimplePermissionDAOImpl implements PermissionDAO {

	private static final G_Permission READ = new G_Permission("read",
			"Read Permission");
	private static final G_Permission WRITE = new G_Permission("write",
			"Write Permission");
	private static final G_Permission EXECUTE = new G_Permission("execute",
			"Execute Permission");
	private static final G_Permission DELETE = new G_Permission("delete",
			"Delete Permission");
	@Inject
	private Logger logger;

	public SimplePermissionDAOImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<G_Permission> getForRole(G_Role role) {
		return getForRole(role.getName());
	}

	@Override
	public List<G_Permission> getForRole(String role) {
		ArrayList<G_Permission> list = new ArrayList<G_Permission>();
		if ("admin".equals(role)) {
			list.add(READ);
			list.add(WRITE);
			list.add(EXECUTE);
			list.add(DELETE);
		} else {
			list.add(READ);
		}
		logger.debug("Returning permissions: " + list);
		return list;
	}

}
