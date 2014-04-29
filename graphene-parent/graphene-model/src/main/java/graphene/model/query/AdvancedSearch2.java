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
	@Override
	public String toString() {
		return "EntitySearchQuery [options=" + options + ", id=" + id
				+ ", queryName=" + queryName + "]";
	}

	private String dataSet = null;
	private String source = null;
	private List<SearchFilter> options;

	public List<SearchFilter> getOptions() {
		return options;
	}

	public void setOptions(List<SearchFilter> options) {
		this.options = options;
	}

	private String id;
	private String queryName;

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void add(SearchFilter option) {
		if (options == null) {
			options = new ArrayList<SearchFilter>(1);
		}
		options.add(option);
	}

	public void remove(SearchFilter option) {
		if (options != null && options.size() > 0) {
			options.remove(option);
		}
	}
}
