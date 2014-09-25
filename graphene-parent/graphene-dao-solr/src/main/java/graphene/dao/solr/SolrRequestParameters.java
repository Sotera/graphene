package graphene.dao.solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery.ORDER;

/**
 * Solr Request Parameter to apply to a query.
 * 
 * @author aweng
 */
public class SolrRequestParameters {

	@Override
	public Object clone() throws CloneNotSupportedException {
		SolrRequestParameters clone = new SolrRequestParameters();
		clone.orderMaps = new HashMap<String, ORDER>();

		clone.orderMaps.putAll(orderMaps);
		clone.resultFields = new ArrayList<String>();
		clone.resultFields.addAll(resultFields);
		clone.rows = rows;
		clone.startRow = startRow;

		return clone;
	}

	private List<String> resultFields = new ArrayList<String>();
	private Map<String, ORDER> orderMaps = new HashMap<String, ORDER>();
	private int startRow;
	private int rows;

	/**
	 * 
	 * @return number of Rows the query going to get at a time. default is 10
	 */
	public int getNumberOfRows() {
		return rows;
	}

	/**
	 * 
	 * @param rows
	 *            number of rows the query going to get if the rows is not set,
	 *            the query will return 10, which is default in solr
	 */
	public void setNumberOfRows(int rows) {
		this.rows = rows;
	}

	/**
	 * 
	 * @return starting row of query
	 */
	public int getStartRow() {
		return startRow;
	}

	/**
	 * Set starting Row of query. this can be used to control the paging
	 * function of query default is 0
	 * 
	 * @param startRow
	 */
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	/**
	 * 
	 * @return Hashmap Map of all ordered field in query
	 */
	public Map<String, ORDER> getOrderMaps() {
		return orderMaps;
	}

	/**
	 * Apply sorting field to all future query. can be call multiple time to
	 * apply different fields
	 * 
	 * @param fieldName
	 *            field Name to sort by
	 * @param order
	 *            Order to field by
	 */
	public void addSortField(String fieldName, ORDER order) {
		orderMaps.put(fieldName, order);
	}

	/**
	 * 
	 * @return result fields
	 */
	public List<String> getResultFields() {
		return resultFields;
	}

	/**
	 * 
	 * @param resultFields
	 *            result fields that query should return. same as solr query
	 *            "fl" filter
	 */
	public void setResultFields(List<String> resultFields) {
		this.resultFields = resultFields;

	}

}