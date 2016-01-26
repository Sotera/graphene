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

@Deprecated
public class Tuple<A, B> {
	private A first;
	private B second;

	public Tuple() {
		// TODO Auto-generated constructor stub
	}

	public Tuple(final A a, final B b) {
		first = a;
		second = b;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Tuple other = (Tuple) obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		return true;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((first == null) ? 0 : first.hashCode());
		result = (prime * result) + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/**
	 * Convenience method for setting both at the same time, but not at
	 * construction time.
	 * 
	 * @param a
	 * @param b
	 */
	public void set(final A a, final B b) {
		first = a;
		second = b;
	}

	public void setFirst(final A first) {
		this.first = first;
	}

	public void setSecond(final B second) {
		this.second = second;
	}

}
