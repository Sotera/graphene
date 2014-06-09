package graphene.services;

import graphene.dao.FederatedEventGraph;

import java.util.Collection;

public class FederatedEventGraphImpl implements FederatedEventGraph {
	private Collection<EventGraphBuilder> singletons;

	public FederatedEventGraphImpl(Collection<EventGraphBuilder> singletons) {
		this.singletons = singletons;
	}


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
		for (EventGraphBuilder s : singletons) {
			System.out.println("Supports " + s.getSupportedDatasets());
		}
		System.out.println("--------------------------------------");
	}
}
