package graphene.ingest;

import graphene.dao.UnifiedEntityDAO;
import graphene.util.fs.DiskCache;
import graphene.util.fs.ObjectStreamIterator;
import graphene.util.stats.TimeReporter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * This class acts as an iterator for a table, and, if possible, uses a local
 * disk copy to avoid hitting the [remote] database.
 * 
 * @author djue
 * 
 * @param <GUnifiedEntities101>
 */
public class UnifiedEntityIterator<GUnifiedEntities101> {

	private static final long MAX_RESULTS = -1;
	private UnifiedEntityDAO dao;

	@Inject
	private DiskCache<?> diskCache;

	@Inject
	private Logger logger;

	public UnifiedEntityIterator() {
		// TODO Auto-generated constructor stub
	}

	public UnifiedEntityIterator(Logger logger2, DiskCache<?> diskCache2,
			UnifiedEntityDAO<?, ?> dao2) {
		// TODO Auto-generated constructor stub
		logger = logger2;
		diskCache = diskCache2;
		dao = dao2;
	}

	/**
	 * 
	 * @param deleteExisting
	 * @param trySerialized
	 * @param saveLocally
	 * @return
	 */
	public ObjectStreamIterator<GUnifiedEntities101> getIterator(
			boolean deleteExisting, boolean trySerialized, boolean saveLocally,
			String optionalFileName) {
		ObjectInputStream s = null;

		// if delete existing, try to do that.
		// if try serialized, try to do that.
		// else read in data to new file, then return an iterator on that file.
		if (deleteExisting) {
			if (diskCache.dropExisting(optionalFileName)) {
				logger.debug("Deleted existing file");
			} else {
				logger.warn("Could not delete existing file");
			}
		}
		if (trySerialized) {
			s = diskCache.restoreFromDisk(optionalFileName);
			if (s != null) {
				logger.debug("Serialized version found!");
				return new ObjectStreamIterator<GUnifiedEntities101>(s);
			} else {
				logger.debug("Serialized version not found, reading from DB");
			}
		}
		// this is not in an else block because we read from the db as a
		// secondary option.
		TimeReporter tr = new TimeReporter("Loading from database", logger);
		if (saveLocally) {

			ObjectOutputStream objStream = diskCache
					.getObjectStream(optionalFileName);
			dao.performCallback(0, MAX_RESULTS, diskCache, null);
			diskCache.closeStreams();
		} else {
			logger.error("Could not read from database!");
		}
		tr.logAsCompleted();
		s = diskCache.restoreFromDisk(optionalFileName);
		return new ObjectStreamIterator<GUnifiedEntities101>(s);
	}
}
