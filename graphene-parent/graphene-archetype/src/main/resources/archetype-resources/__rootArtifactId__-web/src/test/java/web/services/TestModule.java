#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.services;

import ${package}.dao.${artifactId}DAOModule;
import ${package}.model.graphserver.GraphServerModule;
import graphene.util.UtilModule;

import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.TapestryModule;

@SubModule({ TapestryModule.class, AppModule.class, GraphServerModule.class, ${artifactId}DAOModule.class, UtilModule.class })
public class TestModule {

}