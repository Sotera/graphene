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

package graphene.util.fs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic object stream iterator, for use in iterating over disk cache copies
 * of data tables.
 * 
 * @author djue
 * 
 * @param <T>
 */
public class ObjectStreamIterator<T> implements Iterator<T> {
	private final ObjectInputStream s;
	private boolean atEndOfFile = false;
	private final Logger logger = LoggerFactory.getLogger(ObjectStreamIterator.class);

	public ObjectStreamIterator(final ObjectInputStream s) {
		this.s = s;
	}

	public void close() {
		atEndOfFile = true;
		try {
			s.close();
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public boolean hasNext() {
		return !atEndOfFile;
	}

	@Override
	public T next() {
		if (s != null) {
			try {
				return (T) s.readObject();
			} catch (ClassNotFoundException | IOException e) {
				close();
			}
		}
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This method is not available for this iterator");
	}

}
