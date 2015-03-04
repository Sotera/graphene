package graphene.dao;

import graphene.model.diskcache.DiskCache;
import graphene.model.idl.G_SearchResult;
import graphene.model.query.G_CallBack;
import graphene.util.stats.TimeReporter;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public abstract class DiskCacheDAO<T, Q> {
	protected String cacheFileLocation = null;
	protected boolean deleteExisting = false;

	protected DiskCache<T, Q> diskCache;

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

	public abstract boolean getCacheToDisk(boolean deleteExisting, boolean trySerialized, boolean saveLocally,
			long maxResults);

	/**
	 * @return the diskCache
	 */
	public final DiskCache<T, Q> getDiskCache() {
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

	public boolean performCallback(final long offset, final long maxResults, final G_CallBack cb, final Q q) {
		logger.debug("Performing callback at the Disk Cache level...");

		final boolean readerAvailable = getCacheToDisk(deleteExisting, trySerialized, saveLocally, maxResults);
		if (readerAvailable) {
			logger.debug("Disk cache thinks there are " + diskCache.getNumberOfRecordsCached() + " records to read.");
			T e;
			long numProcessed = 0;
			final TimeReporter t = new TimeReporter("Performing callbacks on all rows for disk cache", logger);
			while (readerAvailable && ((e = diskCache.read()) != null)) {
				// logger.debug("read from disk " + e);
				final G_SearchResult sr = G_SearchResult.newBuilder().setResult(e).build();
				cb.callBack(sr);
				numProcessed++;
				if ((numProcessed % 1000) == 0) {
					t.getSpeed(numProcessed, "Disk Cache Objects");
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
	public final void setCacheFileLocation(final String cacheFileLocation) {
		this.cacheFileLocation = cacheFileLocation;
	}

	/**
	 * @param deleteExisting
	 *            the deleteExisting to set
	 */
	public final void setDeleteExisting(final boolean deleteExisting) {
		this.deleteExisting = deleteExisting;
	}

	/**
	 * @param diskCache
	 *            the diskCache to set
	 */
	public final void setDiskCache(final DiskCache<T, Q> diskCache) {
		this.diskCache = diskCache;
	}

	/**
	 * @param maxResults
	 *            the maxResults to set
	 */
	public final void setMaxResults(final long maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @param saveLocally
	 *            the saveLocally to set
	 */
	public final void setSaveLocally(final boolean saveLocally) {
		this.saveLocally = saveLocally;
	}

	/**
	 * @param trySerialized
	 *            the trySerialized to set
	 */
	public final void setTrySerialized(final boolean trySerialized) {
		this.trySerialized = trySerialized;
	}
}
