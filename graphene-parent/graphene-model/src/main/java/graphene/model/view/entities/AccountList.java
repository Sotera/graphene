package graphene.model.view.entities;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AccountList {

	private String error = "No Error";

	private Set<String> results = new HashSet<String>();

	private String status;

	public AccountList() {

	}

	public void addAccount(String ac) {
		results.add(ac);
	}

	public String getError() {
		return error;
	}
	@XmlElementWrapper(name = "matches")
	@XmlElement(name = "account")
	public Set<String> getResults() {
		return results;
	}

	public String getStatus() {
		return status;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setResults(Set<String> results) {
		this.results = results;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
