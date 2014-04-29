package graphene.model.view.namesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NameSearchResponse {

	public String status;
	public String error = "No Error";
	public int count = 0;

	@XmlElementWrapper(name = "matches")
	@XmlElement(name = "name")
	public List<NameSearchResult> results = new ArrayList<NameSearchResult>();

	public List<NameSearchResult> getResults() {
		return results;
	}

	public NameSearchResponse() {

	}

	public void addResult(final NameSearchResult result) {
		results.add(result);
	}

	public void addResult(final String name, final int score, final int index) {
		results.add(new NameSearchResult(name, score, index));
	}

	public void addResults(final Collection<NameSearchResult> r) {
		results.addAll(r);
	}

	public void clearResults() {
		results.clear();
	}

	public void setCount(final int count) {
		this.count = count;
	}

	public void sortResults() {
		Collections.sort(results);
	}

}
