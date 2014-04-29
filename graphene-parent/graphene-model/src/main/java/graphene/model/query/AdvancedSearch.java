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
	// Do not rename these properties. They are used in the REST call
	private String dataSet = null;
	private String source = null;
	private List<SearchFilter> filters = new ArrayList<SearchFilter>();
	static Logger logger = LoggerFactory.getLogger(AdvancedSearch.class);

	/**
	 * Call this after parsing the request string so that the field definition
	 * can be retrieved for each filter
	 * 
	 * @param dataSourceListDAO
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

	public String getDataSet() {
		return dataSet;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<SearchFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<SearchFilter> filters) {
		this.filters = filters;
	}

}