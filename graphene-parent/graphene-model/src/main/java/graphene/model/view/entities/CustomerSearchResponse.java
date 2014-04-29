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
@Deprecated
public class CustomerSearchResponse {

	private String error = "No Error";
	private List<String> matches = new ArrayList<String>();
	
	@XmlElementWrapper(name = "matches")
	@XmlElement(name = "name")
	private List<CustomerDetails> results = new ArrayList<CustomerDetails>();
	private String status;	
	
	public CustomerSearchResponse()
	{
		
	}
	public void addResult(final CustomerDetails result)
	{
		results.add(result);
	}
	public void addResult(final String erm)
	{
		results.add(new CustomerDetails(erm));
	}
	public void addResults(final Collection<CustomerDetails> r)
	{
		results.addAll(r);
	}
	public void clearResults()
	{
		results.clear();
	}
	public void dedupe(final int maxResults)
	{
		Set<CustomerDetails> st = new LinkedHashSet<CustomerDetails>(); // preserves input order
		for (CustomerDetails s:results) {
			st.add(s);
			if (st.size() >= maxResults)
				break;
		}
		results.clear();
		results.addAll(st);
	}
	public int getCount()
	{
		if (results == null)
			return matches.size();
		else
			return results.size();
	}
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @return the matches
	 */
	public List<String> getMatches() {
		return matches;
	}
	public List<CustomerDetails> getResults()
	{
		return results;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(final String error) {
		this.error = error;
	}

	/**
	 * @param matches the matches to set
	 */
	public void setMatches(final List<String> matches) {
		this.matches = matches;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(final List<CustomerDetails> results) {
		this.results = results;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(final String status) {
		this.status = status;
	}
	
}
