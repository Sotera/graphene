package graphene.model.view.entities;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
/**
 * @author PWG for DARPA
 *
 */
public class EntitySearchResults {

	public String status;
	public String error = "No Error";
	public int count;

	// The following was working, but it seems a new json constructor
	// has been added that no longer
	@XmlElementWrapper(name = "entityList")
	@XmlElement(name = "entity")
	public List<EntityLight> results = new ArrayList<EntityLight>();

	public List<EntityLight> getResults() {
		return results;
	}

	public EntitySearchResults() {

	}

	public int getCount() {
		return results.size();
	}

	public void addEntityLight(final EntityLight result) {
		results.add(result);
		count++;
	}

	public void addEntities(final List<EntityLight> entities) {
		for (EntityLight e : entities) {
			addEntityLight(e);
		}
	}

	public void clearResults() {
		results.clear();
	}

	//XXX: This is dumb.  Just store it in a input ordered set to begin with.
	public void dedupe(final int maxResults) {
		Set<EntityLight> st = new LinkedHashSet<EntityLight>(); // preserves
																// input order
		for (EntityLight s : results) {
			st.add(s);
			if (st.size() >= maxResults)
				break;
		}
		results.clear();
		results.addAll(st);
	}

}
