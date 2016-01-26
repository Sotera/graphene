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

public class JVMHelper {
	private static final Runtime runtime = Runtime.getRuntime();

	/**
	 * Aggressively suggest to free memory, then return the amount of free
	 * memory in the system.
	 * 
	 * @return
	 */
	public static long getFreeMem() {
		doGarbage();
		return runtime.freeMemory();
	}

	public static void immolativeShutdown() {
		suggestGC();
		ThreadLocalImmolater i = new ThreadLocalImmolater();
		i.immolate();
	}

	/**
	 * Aggressively suggest to free memory, then returns the total amount of
	 * memory in the Java virtual machine
	 * 
	 * @return
	 */
	public static long getTotalMem() {
		doGarbage();
		return runtime.totalMemory();
	}

	private static void doGarbage() {
		collectGarbage();
		collectGarbage();
	}

	/*
	 * SUGGEST to run the garbage collector. Remember, we can only suggest that
	 * it be called, we can't force it to be done.
	 */
	public static void suggestGC() {
		runtime.gc();
	}

	/**
	 * 
	 */
	static void collectGarbage() {
		try {
			System.gc();
			Thread.sleep(100);
			System.runFinalization();
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

	}

}
