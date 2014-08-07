package graphene.model.query;

import graphene.model.datasourcedescriptors.DataSourceList;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is more of a decorator around {@link EntityDAO}. It also has a
 * questionable practice of setting fields via a JSON mapper, while at the same
 * time being the executor of dao methods.
 * 
 * Changed so this class doesn't hold references to DAO objects.
 * 
 * @author PWG, djue
 * 
 */
public class AdvancedSearch {
	static Logger logger = LoggerFactory.getLogger(AdvancedSearch.class);
	// Do not rename these properties. They are used in the REST call
	private String dataSet = null;
	private List<SearchFilter> filters = new ArrayList<SearchFilter>();
	private int limit = 1000;
	private String source = null;
	private int start = 0;

	public AdvancedSearch() {
		// TODO Auto-generated constructor stub
	}

	public String getDataSet() {
		return dataSet;
	}

	public List<SearchFilter> getFilters() {
		return filters;
	}

	/**
	 * @return the limit
	 */
	public final int getLimit() {
		return limit;
	}

	public String getSource() {
		return source;
	}

	/**
	 * @return the start
	 */
	public final int getStart() {
		return start;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}

	//
	/**
	 * Call this after parsing the request string so that the field definition
	 * can be retrieved for each filter
	 * 
	 * 
	 * @param dataSourceList
	 */
	public void setFieldsIntoFilters(DataSourceList dataSourceList) {
		if (dataSourceList == null) {
			logger.error("Could not get data source list");
		} else {
			for (SearchFilter f : filters) {
				f.setField(dataSourceList.getField(source, dataSet,
						f.getFieldName()));
			}
		}
	}

	public void setFilters(List<SearchFilter> filters) {
		this.filters = filters;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	public final void setLimit(int limit) {
		this.limit = limit;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public final void setStart(int start) {
		this.start = start;
	}

	@Override
	public String toString() {
		return "AdvancedSearch ["
				+ (dataSet != null ? "dataSet=" + dataSet + ", " : "")
				+ (source != null ? "source=" + source + ", " : "")
				+ (filters != null ? "filters=" + filters : "") + "]";
	}

}