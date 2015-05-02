package graphene.services;

import graphene.dao.StartupProcedures;

/**
 * A No Op implementation that does nothing.
 * 
 * @author djue
 * 
 */
public class StartupProceduresNoOpImpl implements StartupProcedures {

	@Override
	public boolean initialize() {
		final boolean success = true;

		return success;
	}
}
