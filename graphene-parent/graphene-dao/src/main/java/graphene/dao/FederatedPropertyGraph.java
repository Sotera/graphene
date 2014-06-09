package graphene.dao;

import graphene.services.PropertyGraphBuilder;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

@UsesConfiguration(PropertyGraphBuilder.class)
public interface FederatedPropertyGraph {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	public abstract void printDatasetsSupported();

}