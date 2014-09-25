package graphene.dao;

import graphene.services.AbstractGraphBuilder;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

@UsesConfiguration(AbstractGraphBuilder.class)
public interface FederatedPropertyGraphServer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	public abstract void printDatasetsSupported();

}