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
