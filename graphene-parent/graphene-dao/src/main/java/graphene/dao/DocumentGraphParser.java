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
	/**
	 * The key for storing an address for the entity.
	 */
	public static final String LABEL2 = "label2";
	/**
	 * The key for storing a list of dates of the filings in the report. Since
	 * there may be multiple transactions, there can be multiple dates of
	 * filing.
	 */
	public static final String DATES_FILED = "datesfiled";
	/**
	 * The key for storing a list of dates of the events in the report. Since
	 * there may be multiple transactions, there can be multiple dates of
	 * events.
	 */
	public static final String DATES_OF_EVENTS = "datesofevents";
	/**
	 * The key for storing a list of dates received in the report. Since there
	 * may be multiple transactions, there can be multiple dates received.
	 */
	public static final String DATES_RECEIVED = "datesreceived";
	public static final String FIRST_DATE = "firstDate";
	public static final String FLOWIN = "flowIn";
	public static final String FLOWOUT = "flowOut";
	public static final String HIGHLIGHT = "highlight";
	public static final String ICONLIST = "iconlist";
	/**
	 * The key for storing the label used in displaying the entity.
	 */
	public static final String LABEL = "label";
	public static final String LAST_DATE = "lastDate";
	public static final String NARRATIVE = "narrative";

	/**
	 * The key for storing the index+1 number of an item from a list. For
	 * example, 1 for the first entity, etc.
	 */
	public static final String NUMBER = "number";

	/**
	 * The key for storing a phone number for the entity.
	 */
	public static final String PHONE = "phone";

	public static final String REPORT_ID = "reportid";

	public static final String REPORT_LABEL = "reportlabel";
	public static final String REPORT_LINK = "reportlink";

	public static final String REPORT_TYPE = "reporttype";
	public static final String SCORE = "score";
	public static final String SUBJECTADDRESSLIST = "subjectaddresslist";
	public static final String SUBJECTCIDLIST = "subjectcidlist";
	public static final String SUBJECTIDLIST = "subjectidlist";
	public static final String SUBJECTNAMELIST = "subjectnamelist";
	/**
	 * The key for storing the numeric amount of money involved in the report,
	 * in a format that can be sorted and scaled.
	 */
	public static final String TOTALAMOUNTNBR = "totalAmountNbr";
	/**
	 * The key for storing the amount of money involved in the report. Usually
	 * stored at the top level of the report and is pre-formatted.
	 */
	public static final String TOTALAMOUNTSTR = "totalamountinvolved";
	public static final String CARDINAL_ORDER = "cardinalOrder";
	
	
	public static final String MEDIA_ID = "mediaid";
	public static final String MEDIA_LABEL = "medialabel";
	public static final String MEDIA_LINK = "medialink";
	public static final String MEDIA_OWNER = "mediaowner";
	public static final String MEDIA_CREATED_TIME = "mediacreatedtime";
	public static final String MEDIA_CAPTION_TEXT = "mediacaptiontext";
	public static final String MEDIA_LIKE_COUNT = "medialikecount";
	public static final String MEDIA_COMMENT_COUNT = "mediacommentcount";
	public static final String MEDIA_LOCATION_LATLON = "medialocationlatlon";
	public static final String MEDIA_LOCATION_NAME = "medialocationname";	

	public Map<String, Object> getAdditionalProperties(Object obj);

	/**
	 * @return the phgb
	 */
	public HyperGraphBuilder<Object> getPhgb();

	public List<String> getSupportedObjects();

	/**
	 * We try cast the object into a particular class, and pull out the
	 * information we want. The Query object q is provided so that we know which
	 * query allowed us to arrive at this object. This can be useful for
	 * highlighting fields that were in the original query. The Query object
	 * also holds a key value for a potential node that was assosicated with the
	 * query. In this way, we can tie the resulting object back to a node id
	 * that initiated the query.
	 * 
	 * @param obj
	 *            a result of a query specified by q.
	 * @param q
	 *            the query object that generated this result, along with an
	 *            associated node id.
	 * @return
	 */
	public boolean parse(Object obj, EntityQuery q);

	public abstract T populateExtraFields(T theEvent, EntityQuery q);

	/**
	 * @param phgb
	 *            the phgb to set
	 */
	public void setPhgb(PropertyHyperGraphBuilder<Object> phgb);

}