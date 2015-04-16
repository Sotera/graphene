package graphene.model.extracted;

import java.util.HashMap;
import java.util.Map;

public class KVMapping {
	private Map<String, String> kvpairs;

	public KVMapping() {
		kvpairs = new HashMap<String, String>();
	}

	/**
	 * @return the kvpairs
	 */
	public Map<String, String> getKvpairs() {
		return kvpairs;
	}

	/**
	 * @param kvpairs
	 *            the kvpairs to set
	 */
	public void setKvpairs(final Map<String, String> kvpairs) {
		this.kvpairs = kvpairs;
	}

}
