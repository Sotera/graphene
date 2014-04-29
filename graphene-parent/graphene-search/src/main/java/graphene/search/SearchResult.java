package graphene.search;

public class SearchResult implements Comparable<SearchResult>{
	String str;
	int score;
	
	public SearchResult(String str, int score)
	{
		this.str = str;
		this.score = score;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	public int compareTo(SearchResult r)
	{
		if (r.score < score)
			return -1;
		if (r.score> score)
			return 1;
		return str.compareTo(r.str);
	}
	
	
}
