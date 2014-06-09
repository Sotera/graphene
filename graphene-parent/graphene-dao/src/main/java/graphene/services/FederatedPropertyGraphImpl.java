package graphene.services;

import graphene.dao.FederatedPropertyGraph;

import java.util.Collection;

public class FederatedPropertyGraphImpl implements FederatedPropertyGraph {
	private Collection<PropertyGraphBuilder> singletons;
	
	public FederatedPropertyGraphImpl(Collection<PropertyGraphBuilder> singletons) {
		this.singletons = singletons;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.dea.contribute.FederatedGraph#toString()
	 */
	@Override
	public String toString() {
		return "FederatedGraph ["
				+ (singletons != null ? "singletons=" + singletons : "") + "]";
	}

	@Override
	public void printDatasetsSupported() {
		System.out.println("--------------------------------------");
		System.out.println("FEDERATED GRAPH DATASETS -------------");
		System.out.println("--------------------------------------");
		for (PropertyGraphBuilder s : singletons) {
			System.out.println("Supports " + s.getSupportedDatasets());
		}
		System.out.println("--------------------------------------");
	}
}
