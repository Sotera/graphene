package graphene.model.query;

import graphene.model.idl.G_SearchTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a query object where we explicitly define lists of ids for senders
 * and receivers, so that we can account for directionality in the query.  If you don't care about directionality, it may be easier to use EventQuery.
 * 
 * 
 * Expect
 * a query string like:
 * Sources=<SearchTypeNumber><SearchTypeDelimiter><identifier
 * ><<MultipleItemDelimiter
 * ><SearchTypeNumber><SearchTypeDelimiter><identifier>>&
 * Destinations=<SearchTypeNumber><SearchTypeDelimiter><identifier>
 * 
 * example: Sources contains 'apple' or sounds like 'berry' and Destinations
 * starts with 'pie' or starts with 'cake'
 * 
 * would look like this: Sources=1:apple,4:berry&Destinations=0:pie,0:cake
 * 
 * 
 * TODO: Consider adding flags to search for Sources and to identifiers inside
 * payloads. This may speed things up considerably if the identifiers are small
 * and common, or if there are many or large payloads per record. For instance
 * if this were email, the flag would indicate whether to search the body of the
 * emails (payloads) for identifiers supplied in the Destinations/Sources
 * fields.
 * 
 * 
 * 
 * @author djue
 * 
 */
public class DirectedEventQuery extends BasicQuery implements IntersectionQuery {

	private List<G_SearchTuple<String>> sources = new ArrayList<G_SearchTuple<String>>(
			2);
	private List<G_SearchTuple<String>> destinations = new ArrayList<G_SearchTuple<String>>(
			2);
	private List<G_SearchTuple<String>> payloadKeywords = new ArrayList<G_SearchTuple<String>>(
			1);

	private boolean intersectionOnly = false;
	private boolean searchPayloadsForSources = false;
	private boolean searchPayloadsForDestinations = false;

	public boolean isSearchPayloadsForSources() {
		return searchPayloadsForSources;
	}

	public void setSearchPayloadsForSources(boolean searchPayloadsForSources) {
		this.searchPayloadsForSources = searchPayloadsForSources;
	}

	public boolean isSearchPayloadsForDestinations() {
		return searchPayloadsForDestinations;
	}

	public void setSearchPayloadsForDestinations(
			boolean searchPayloadsForDestinations) {
		this.searchPayloadsForDestinations = searchPayloadsForDestinations;
	}

	public boolean isIntersectionOnly() {
		return intersectionOnly;
	}

	public void setIntersectionOnly(boolean intersectionOnly) {
		this.intersectionOnly = intersectionOnly;
	}

	public List<G_SearchTuple<String>> getSources() {
		return sources;
	}

	public void setSources(List<G_SearchTuple<String>> sources) {
		this.sources = sources;
	}

	public List<G_SearchTuple<String>> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<G_SearchTuple<String>> destinations) {
		this.destinations = destinations;
	}

	public List<G_SearchTuple<String>> getPayloadKeywords() {
		return payloadKeywords;
	}

	public void setPayloadKeywords(List<G_SearchTuple<String>> payloadKeywords) {
		this.payloadKeywords = payloadKeywords;
	}

}
