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

package graphene.ingest.batchoptimizers;

import com.google.common.hash.BloomFilter;

public class BasicBatchOptimizer {
	/**
	 * Checks to see if the relationship is unique, and if it is, adds it to the
	 * bloom filter.
	 * 
	 * @param from
	 * @param to
	 * @param rel
	 * @return
	 */
	public boolean is_unique_rel(BloomFilter<String> bf, Long from, String rel,
			Long to) {
		String key = "" + from + to + rel;
		if (bf.mightContain(key)) {
			return false;
		} else {
			bf.put(key);
			return true;
		}
	}
	
	
}
