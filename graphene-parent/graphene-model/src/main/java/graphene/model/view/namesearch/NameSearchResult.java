package graphene.model.view.namesearch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class NameSearchResult implements Comparable<Object> {
	@XmlElement
	public String name;
	@XmlElement
	public int score;
	@XmlElement
	public int index;

	public NameSearchResult() {

	}

	public NameSearchResult(final String name,final  int score,final  int index) {
		this.name = name;
		this.score = score;
		this.index = index;
	}

	@Override
	public int compareTo(final Object o) {
		NameSearchResult r = (NameSearchResult) o;
		if (r.score == score)
			return name.compareToIgnoreCase(r.name);
		else if (r.score > score)
			return 1;
		else
			return -1;
	}
}
