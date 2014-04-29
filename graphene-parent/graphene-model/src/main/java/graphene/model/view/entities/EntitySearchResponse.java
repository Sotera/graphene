package graphene.model.view.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EntitySearchResponse {

	private String status;
	private String error = "No Error";

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(final String error) {
		this.error = error;
	}

	public List<String> getMatches() {
		return matches;
	}

	public void setMatches(final List<String> matches) {
		this.matches = matches;
	}

	public void setResults(final List<CustomerDetails> results) {
		this.results = results;
	}

	// will be a list of Entities
	private List<CustomerDetails> results = new ArrayList<CustomerDetails>();
	private List<String> matches = new ArrayList<String>();

	@XmlElementWrapper(name = "matches")
	@XmlElement(name = "name")
	public List<CustomerDetails> getResults() {
		return results;
	}

	public EntitySearchResponse() {

	}

	public int getCount() {
		if (results == null)
			return matches.size();
		else
			return results.size();
	}

	public void addResult(final CustomerDetails result) {
		results.add(result);
	}

	public void addResult(final String erm) {
		results.add(new CustomerDetails(erm));
	}

	public void addResults(final Collection<CustomerDetails> r) {
		results.addAll(r);
	}

	public void clearResults() {
		results.clear();
	}

	public void dedupe(final int maxResults) {
		Set<CustomerDetails> st = new LinkedHashSet<CustomerDetails>(); // preserves
																		// input
																		// order
		for (CustomerDetails s : results) {
			st.add(s);
			if (st.size() >= maxResults)
				break;
		}
		results.clear();
		results.addAll(st);
	}

}
