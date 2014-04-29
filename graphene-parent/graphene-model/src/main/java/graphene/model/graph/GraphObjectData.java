package graphene.model.graph;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/*
 * Each Node or Attribute in graphml can contain zero to many Data
 * elements of form <data key=foo>value</data>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class GraphObjectData implements Comparable<GraphObjectData> {
	@XmlAttribute
	String key;
	@XmlValue
	String keyVal;

	public GraphObjectData(String key, String keyVal) {
		this.key = key;
		this.keyVal = keyVal;
	}

	public GraphObjectData() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphObjectData other = (GraphObjectData) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKeyVal() {
		return keyVal;
	}

	public void setKeyVal(String keyVal) {
		this.keyVal = keyVal;
	}

	@Override
	public int compareTo(GraphObjectData o) {
		int keyCompare = key.compareTo(o.key);
		if (keyCompare == 0) {
			return keyVal.compareTo(o.keyVal);
		} else {
			return keyCompare;
		}

	}
	@Override
	public String toString()
	{
		return key + ":" + keyVal;
	}

	

}
