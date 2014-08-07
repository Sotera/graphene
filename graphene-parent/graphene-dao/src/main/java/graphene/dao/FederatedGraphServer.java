package graphene.dao;

import graphene.services.AbstractGraphBuilder;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

/**
 * Handles all kinds of graph builders
 * 
 * @author djue
 * 
 */
@UsesConfiguration(AbstractGraphBuilder.class)
public interface FederatedGraphServer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	public abstract void printDatasetsSupported();

	public abstract AbstractGraphBuilder getGraphBuilderForDataSource(
			String datasource);

}