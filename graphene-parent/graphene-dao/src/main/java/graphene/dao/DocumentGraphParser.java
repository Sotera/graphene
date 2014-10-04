package graphene.dao;

import graphene.services.HyperGraphBuilder;
import graphene.services.PropertyHyperGraphBuilder;

import java.util.List;

public interface DocumentGraphParser {

	public boolean parse(Object obj);

	/**
	 * @return the phgb
	 */
	public HyperGraphBuilder<Object> getPhgb();

	/**
	 * @param phgb
	 *            the phgb to set
	 */
	public void setPhgb(PropertyHyperGraphBuilder<Object> phgb);

	public List<String> getSupportedObjects();

}