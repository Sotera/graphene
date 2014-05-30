package graphene.dao;

import graphene.util.G_CallBack;
import graphene.util.fs.DiskCache;
import graphene.util.stats.TimeReporter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public abstract class DiskCacheDAO<T, Q> {
	protected final boolean DELETE_EXISTING = false;
	protected final String FILE_NAME = "T:/data/entityRefCache.data";

	protected final long MAX_RESULTS = 0;

	protected final boolean SAVE_LOCALLY = true;

	protected final boolean TRY_SERIALIZED = true;

	public DiskCacheDAO(DiskCache<T> diskCache) {
		this.diskCache = diskCache;
	}

	protected DiskCache<T> diskCache;

	@Inject
	private Logger logger;

	// public long count(Q q) throws Exception {
	// TimeReporter t = new TimeReporter("Counting all rows in DiskCacheDAO",
	// logger);
	//
	// T e = null;
	// if (diskCache.getNumberOfRecordsCached() == 0) {
	// boolean readerAvailable = getCacheToDisk(DELETE_EXISTING,
	// TRY_SERIALIZED, SAVE_LOCALLY, 0);
	// // while (readerAvailable && (e = diskCache.read()) != null) {
	// // // TODO: we are disregarding the query object now, because we
	// // // never use it this way. However, the code should do what you
	// // // would think it does, so put this in the bucket list. --djue
	// // numRows++;
	// // if (numRows % 1000 == 0) {
	// // t.getSpeed(numRows, "Count EnronEntityref100");
	// // }
	// // }
	// diskCache.closeStreams();
	// }
	// return diskCache.getNumberOfRecordsCached();
	// }

	public boolean performCallback(long offset, long maxResults,
			G_CallBack<T> cb, Q q) {
		logger.debug("Performing callback at the Disk Cache level...");

		boolean readerAvailable = getCacheToDisk(DELETE_EXISTING,
				TRY_SERIALIZED, SAVE_LOCALLY, maxResults);
		if (readerAvailable) {
			logger.debug("Disk cache thinks there are "
					+ diskCache.getNumberOfRecordsCached()
					+ " records to read.");
			T e;
			long numProcessed = 0;
			TimeReporter t = new TimeReporter(
					"Performing callbacks on all rows for disk cache", logger);
			while (readerAvailable && (e = diskCache.read()) != null) {
				// logger.debug("read from disk " + e);
				cb.callBack(e);
				numProcessed++;
				if (numProcessed % 1000 == 0) {
					t.getSpeed(numProcessed, "EnronEntityref100");
				}
			}
			t.logElapsed("Done reading with " + numProcessed + " processed.");
			diskCache.closeStreams();
			t.logAsCompleted();
		} else {
			logger.error("A reader was not available, ");
		}
		return readerAvailable;
	}

	public abstract boolean getCacheToDisk(boolean deleteExisting,
			boolean trySerialized, boolean saveLocally, long maxResults);
}
