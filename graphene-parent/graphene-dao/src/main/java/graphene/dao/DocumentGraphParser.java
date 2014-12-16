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
	/**
	 * The key for storing the index+1 number of an item from a list. For
	 * example, 1 for the first entity, etc.
	 */
	public static final String NUMBER = "number";

	/**
	 * The key for storing the amount of money involved in the report. Usually
	 * stored at the top level of the report and is pre-formatted.
	 */
	public static final String TOTALAMOUNTSTR = "totalamountinvolved";

	/**
	 * The key for storing a list of dates received in the report. Since there
	 * may be multiple transactions, there can be multiple dates received.
	 */
	public static final String DATES_RECEIVED = "datesreceived";

	/**
	 * The key for storing a list of dates of the events in the report. Since
	 * there may be multiple transactions, there can be multiple dates of
	 * events.
	 */
	public static final String DATES_OF_EVENTS = "datesofevents";
	/**
	 * The key for storing a list of dates of the filings in the report. Since
	 * there may be multiple transactions, there can be multiple dates of
	 * filing.
	 */
	public static final String DATES_FILED = "datesfiled";

	/**
	 * The key for storing the numeric amount of money involved in the report,
	 * in a format that can be sorted and scaled.
	 */
	public static final String TOTALAMOUNTNBR = null;

	/**
	 * The key for storing the label used in displaying the entity.
	 */
	public static final String LABEL = "label";
	/**
	 * The key for storing an address for the entity.
	 */
	public static final String ADDRESS = "address";
	/**
	 * The key for storing a phone number for the entity.
	 */
	public static final String PHONE = "phone";

	public boolean parse(Object obj, EntityQuery q);

	public abstract T populateExtraFields(T theEvent);

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