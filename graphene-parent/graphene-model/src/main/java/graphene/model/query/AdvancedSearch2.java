package graphene.model.query;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a something I whipped up to test a UI. It is probably already
 * implemented in one of the other classes.
 * 
 * Among the things to change are:
 * 
 * Use enumset to specify a subset of allowable types of searches.
 * 
 * Since the types of allowable searches are different depending on customer and
 * data types, the list must be data driven.
 * 
 * We'll need custom edit blocks (using custom components like daterange
 * pickers, file uploads, etc) for each type of value.
 * 
 * @author djue
 * 
 */

public class AdvancedSearch2 {
	private String dataSet = null;
	private String id;
	private List<SearchFilter> options;
	private String queryName;
	private String source = null;

	public void add(SearchFilter option) {
		if (options == null) {
			options = new ArrayList<SearchFilter>(1);
		}
		options.add(option);
	}

	public String getId() {
		return id;
	}
	public List<SearchFilter> getOptions() {
		return options;
	}

	public String getQueryName() {
		return queryName;
	}

	public void remove(SearchFilter option) {
		if (options != null && options.size() > 0) {
			options.remove(option);
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOptions(List<SearchFilter> options) {
		this.options = options;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	@Override
	public String toString() {
		return "EntitySearchQuery [options=" + options + ", id=" + id
				+ ", queryName=" + queryName + "]";
	}
}
