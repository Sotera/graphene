package graphene.dao;

import graphene.services.EventGraphBuilder;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

@UsesConfiguration(EventGraphBuilder.class)
public interface FederatedEventGraphServer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	public abstract void printDatasetsSupported();

	public abstract EventGraphBuilder getGraphBuilderForDataSource(String datasource);

}