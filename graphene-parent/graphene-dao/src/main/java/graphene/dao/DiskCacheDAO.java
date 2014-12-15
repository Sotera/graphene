package graphene.dao;

import graphene.util.G_CallBack;
import graphene.util.fs.DiskCache;
import graphene.util.stats.TimeReporter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public abstract class DiskCacheDAO<T, Q> {
	protected String cacheFileLocation = null;
	protected boolean deleteExisting = false;

	protected DiskCache<T,Q> diskCache;

	@Inject
	private Logger logger;

	protected long maxResults = 0;

	protected boolean saveLocally = true;

	protected boolean trySerialized = true;

	public DiskCacheDAO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the cacheFileLocation
	 */
	public final String getCacheFileLocation() {
		return cacheFileLocation;
	}

	public abstract boolean getCacheToDisk(boolean deleteExisting,
			boolean trySerialized, boolean saveLocally, long maxResults);

	/**
	 * @return the diskCache
	 */
	public final DiskCache<T,Q> getDiskCache() {
		return diskCache;
	}

	/**
	 * @return the maxResults
	 */
	public final long getMaxResults() {
		return maxResults;
	}

	/**
	 * @return the deleteExisting
	 */
	public final boolean isDeleteExisting() {
		return deleteExisting;
	}

	/**
	 * @return the saveLocally
	 */
	public final boolean isSaveLocally() {
		return saveLocally;
	}

	/**
	 * @return the trySerialized
	 */
	public final boolean isTrySerialized() {
		return trySerialized;
	}

	public boolean performCallback(final long offset, final long maxResults,
			final G_CallBack<T,Q> cb, final Q q) {
		logger.debug("Performing callback at the Disk Cache level...");

		boolean readerAvailable = getCacheToDisk(deleteExisting, trySerialized,
				saveLocally, maxResults);
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

	/**
	 * @param cacheFileLocation
	 *            the cacheFileLocation to set
	 */
	public final void setCacheFileLocation(String cacheFileLocation) {
		this.cacheFileLocation = cacheFileLocation;
	}

	/**
	 * @param deleteExisting
	 *            the deleteExisting to set
	 */
	public final void setDeleteExisting(boolean deleteExisting) {
		this.deleteExisting = deleteExisting;
	}

	/**
	 * @param diskCache
	 *            the diskCache to set
	 */
	public final void setDiskCache(DiskCache<T,Q> diskCache) {
		this.diskCache = diskCache;
	}

	/**
	 * @param maxResults
	 *            the maxResults to set
	 */
	public final void setMaxResults(long maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @param saveLocally
	 *            the saveLocally to set
	 */
	public final void setSaveLocally(boolean saveLocally) {
		this.saveLocally = saveLocally;
	}

	/**
	 * @param trySerialized
	 *            the trySerialized to set
	 */
	public final void setTrySerialized(boolean trySerialized) {
		this.trySerialized = trySerialized;
	}
}
