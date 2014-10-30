package graphene.dao;

import graphene.services.HyperGraphBuilder;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

@UsesConfiguration(HyperGraphBuilder.class)
public interface FederatedPropertyGraphServer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	public abstract void printDatasetsSupported();

}