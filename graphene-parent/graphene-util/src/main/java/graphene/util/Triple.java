/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.util;

import java.io.Serializable;

/**
 * A generic structure for holding a triple of related data
 * 
 * @author djue
 * 
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class Triple<A, B, C> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private A first;

	private B second;

	private C third;

	public Triple() {
		// TODO Auto-generated constructor stub
	}

	public Triple(A a, B b, C c) {
		first = a;
		second = b;
		third = c;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		Triple<A, B, C> other = (Triple<A, B, C>) obj;
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
		if (third == null) {
			if (other.third != null)
				return false;
		} else if (!third.equals(other.third))
			return false;
		return true;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	public C getThird() {
		return third;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		result = prime * result + ((third == null) ? 0 : third.hashCode());
		return result;
	}

	/**
	 * Convenience method for setting both at the same time, but not at
	 * construction time.
	 * 
	 * @param a
	 * @param b
	 */
	public void set(A a, B b, C c) {
		first = a;
		second = b;
		third = c;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	public void setThird(C third) {
		this.third = third;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Triple [" + (first != null ? "first=" + first + ", " : "")
				+ (second != null ? "second=" + second + ", " : "")
				+ (third != null ? "third=" + third : "") + "]";
	}

}
