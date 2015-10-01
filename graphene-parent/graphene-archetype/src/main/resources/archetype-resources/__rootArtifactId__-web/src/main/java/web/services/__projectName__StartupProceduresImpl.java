#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.services;

import graphene.dao.StartupProcedures;

public class ${projectName}StartupProceduresImpl implements StartupProcedures {

	@Override
	public boolean initialize() {
		return true;
	}
}