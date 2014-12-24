package graphene.dao;

import graphene.model.query.EntityQuery;
import graphene.services.HyperGraphBuilder;
import graphene.services.PropertyHyperGraphBuilder;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author djue
 * 
 * @param <T>
 *            the object that you can cast to
 */
public interface DocumentGraphParser<T> {
	public static final String SUBJECTADDRESSLIST = "subjectaddresslist";
	public static final String SUBJECTCIDLIST = "subjectcidlist";
	public static final String NARRATIVE = "narrative";
	public static final String ICONLIST = "iconlist";
	public static final String SUBJECTIDLIST = "subjectidlist";
	public static final String SUBJECTNAMELIST = "subjectnamelist";
	public static final String REPORT_LINK = "reportlink";
	public static final String REPORT_TYPE = "reporttype";
	public static final String REPORT_LABEL = "reportlabel";
	public static final String REPORT_ID = "reportid";
	public static final String HIGHLIGHT = "highlight";
	public static final String SCORE = "score";

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

	public static final String FIRST_DATE = "firstDate";
	public static final String LAST_DATE = "lastDate";
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

	public Map<String, Object> getAdditionalProperties(Object obj);

	/**
	 * @return the phgb
	 */
	public HyperGraphBuilder<Object> getPhgb();

	public List<String> getSupportedObjects();

	public boolean parse(Object obj, EntityQuery q);

	public abstract T populateExtraFields(T theEvent, EntityQuery q);

	/**
	 * @param phgb
	 *            the phgb to set
	 */
	public void setPhgb(PropertyHyperGraphBuilder<Object> phgb);

}