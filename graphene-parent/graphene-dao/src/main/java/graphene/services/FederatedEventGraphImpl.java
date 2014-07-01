package graphene.services;

import graphene.dao.FederatedEventGraph;
import graphene.util.validator.ValidationUtils;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class FederatedEventGraphImpl implements FederatedEventGraph {
	private Collection<EventGraphBuilder> singletons;
	@Inject
	private Logger logger;

	public FederatedEventGraphImpl(Collection<EventGraphBuilder> singletons) {
		this.singletons = singletons;
	}

	/**
	 * This is a rudimentary implementation that just returns the first graph
	 * builder that supports the dataset. Eventually we want to return a
	 * collection of graph builders supporting the dataset.
	 * 
	 * @param datasource
	 * @return
	 */
	@Override
	public EventGraphBuilder getGraphBuilderForDataSource(String datasource) {
		if (ValidationUtils.isValid(datasource)) {
			if (ValidationUtils.isValid(singletons)) {
				for (EventGraphBuilder s : singletons) {
					if (s.supportedDatasets.contains(datasource)) {
						return s;
					}
				}
			} else {
				logger.warn("No graph builders found");
			}
		} else {
			logger.warn("No datasource specified");
		}
		return null;
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
