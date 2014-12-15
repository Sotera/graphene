package graphene.dao;

import graphene.model.query.EntityQuery;
import graphene.services.HyperGraphBuilder;
import graphene.services.PropertyHyperGraphBuilder;

import java.util.List;

/**
 * 
 * @author djue
 * 
 * @param <T>
 *            the object that you can cast to
 */
public interface DocumentGraphParser<T> {

	public boolean parse(Object obj, EntityQuery q);
	public abstract T populateExtraFields(T theEvent);
	public String calculateTotal(T theEvent);

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