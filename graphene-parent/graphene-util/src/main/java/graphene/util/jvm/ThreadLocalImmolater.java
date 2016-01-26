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

package graphene.util.jvm;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Administrator
 * 
 *         google-gson issue # 402: Memory Leak in web application; comment # 25
 *         https://code.google.com/p/google-gson/issues/detail?id=402
 */
public class ThreadLocalImmolater {

	final Logger logger = LoggerFactory.getLogger(ThreadLocalImmolater.class);

	Boolean debug;

	public ThreadLocalImmolater() {
		debug = true;
	}

	public Integer immolate() {
		int count = 0;
		try {
			final Field threadLocalsField = Thread.class
					.getDeclaredField("threadLocals");
			threadLocalsField.setAccessible(true);
			final Field inheritableThreadLocalsField = Thread.class
					.getDeclaredField("inheritableThreadLocals");
			inheritableThreadLocalsField.setAccessible(true);
			for (final Thread thread : Thread.getAllStackTraces().keySet()) {
				count += clear(threadLocalsField.get(thread));
				count += clear(inheritableThreadLocalsField.get(thread));
			}
			logger.info("immolated " + count + " values in ThreadLocals");
		} catch (Exception e) {
			throw new Error("ThreadLocalImmolater.immolate()", e);
		}
		return count;
	}

	private int clear(final Object threadLocalMap) throws Exception {
		if (threadLocalMap == null)
			return 0;
		int count = 0;
		final Field tableField = threadLocalMap.getClass().getDeclaredField(
				"table");
		tableField.setAccessible(true);
		final Object table = tableField.get(threadLocalMap);
		for (int i = 0, length = Array.getLength(table); i < length; ++i) {
			final Object entry = Array.get(table, i);
			if (entry != null
					&& WeakReference.class.isAssignableFrom(entry.getClass())) {
				WeakReference<Object> wr = (WeakReference<Object>) entry;
				final Object threadLocal = wr.get();
				if (threadLocal != null) {
					log(i, threadLocal);
					Array.set(table, i, null);
					++count;
				}
			}
		}
		return count;
	}

	private void log(int i, final Object threadLocal) {
		if (!debug) {
			return;
		}
		if (threadLocal.getClass() != null
				&& threadLocal.getClass().getEnclosingClass() != null
				&& threadLocal.getClass().getEnclosingClass().getName() != null) {

			logger.info("threadLocalMap(" + i + "): "
					+ threadLocal.getClass().getEnclosingClass().getName());
		} else if (threadLocal.getClass() != null
				&& threadLocal.getClass().getName() != null) {
			logger.info("threadLocalMap(" + i + "): "
					+ threadLocal.getClass().getName());
		} else {
			logger.info("threadLocalMap(" + i
					+ "): cannot identify threadlocal class name");
		}
	}

}