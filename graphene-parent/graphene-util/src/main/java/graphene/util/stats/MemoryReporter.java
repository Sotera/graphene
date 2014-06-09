package graphene.util.stats;

import org.slf4j.Logger;

public class MemoryReporter {
	// Set when the reporter was created.
	private long startBytesUsed;
	private long lastBytesUsed;
	private Logger log;
	private String task;
	private static final long MEGABYTE = 1024L * 1024L;
	private static final Runtime runtime = Runtime.getRuntime();

	public MemoryReporter(String t, Logger logger) {
		log = logger;
		task = t;
		init();
	}

	private void init() {
		startBytesUsed = getBytesUsed();
		lastBytesUsed = startBytesUsed;
	}

	public long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	/**
	 * Warning: this suggests to the JVM that the GC be called.
	 * 
	 * @return
	 */
	public long getBytesUsed() {
		// Calculate the used memory
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public long getFreeBytes() {
		return runtime.freeMemory();
	}

	public long getFreeMegabytes() {
		return bytesToMegabytes(getFreeBytes());
	}

	public long getMegabytesUsed() {
		return bytesToMegabytes(getBytesUsed());
	}

	public void reportBytesUsed() {
		System.out.println("Used memory in bytes for " + task + ": "
				+ getBytesUsed());
	}

	public void reportMegabytesUsed() {
		System.out.println("Used memory in megabytes for " + task + ": "
				+ getMegabytesUsed());
	}

	/**
	 * Reports the amount of MB used since the initialization of this reporter.
	 * Could be negative.
	 * 
	 * @param string
	 */
	public void logMemoryUsedByEvent(String string) {
		long megs = (getMegabytesUsed() - bytesToMegabytes(startBytesUsed));
		log.debug("Memory used by " + string + " = " + megs);
	}

	/**
	 * Report the amount of GB currently in use by the system. (doesn't care
	 * what it was before)
	 * 
	 * @param string
	 */
	public void logGigabytesUsed(String string) {
		log.debug("Total Memory in use " + string + " = " + getMegabytesUsed()
				/ 1024d + "GB");
	}

	/**
	 * Report the amount of MB currently in use by the system. (doesn't care
	 * what it was before)
	 * 
	 * @param string
	 */
	public void logMegabytesUsed(String string) {
		log.debug("Total Memory in use " + string + " = " + getMegabytesUsed()
				+ "MB");
	}

	/**
	 * Report the amount of Bytes currently in use by the system. (doesn't care
	 * what it was before)
	 * 
	 * @param string
	 */
	public void logBytesUsed(String string) {
		log.debug("Total Memory in use " + string + " = " + getBytesUsed()
				+ "B");
	}

	/**
	 * Reports the amount of MB currently in use by the system, divided by the
	 * number of items you processed.
	 * 
	 * Logs something like "Used 64.7 bytes/doodad"
	 * 
	 * @param item
	 * @param numItems
	 */
	public void logBytesUsedPerItem(String item, long numItems) {
		if (numItems > 0) {
			log.debug("For " + task + ": " + numItems + ", Used "
					+ (getBytesUsed() - startBytesUsed) / (double) numItems
					+ " bytes/" + item);
		} else {
			// avoid divide by zero
			log.debug("For " + task + ": " + numItems + ", Used "
					+ (getBytesUsed() - startBytesUsed));
		}
	}

	public void reset() {
		init();
	}

}
