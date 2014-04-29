package graphene.util;

public class Tuple<A, B> {
	private A first;
	private B second;

	public Tuple() {
		// TODO Auto-generated constructor stub
	}

	public Tuple(A a, B b) {
		first = a;
		second = b;
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	/**
	 * Convenience method for setting both at the same time, but not at
	 * construction time.
	 * 
	 * @param a
	 * @param b
	 */
	public void set(A a, B b) {
		first = a;
		second = b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

}
