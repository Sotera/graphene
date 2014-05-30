package graphene.ingest;

import graphene.dao.UnifiedEntityDAO;
import graphene.util.fs.DiskCache;
import graphene.util.fs.JavaDiskCache;

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

	public UnifiedEntityIterator(Logger logger2, JavaDiskCache<?> diskCache2,
			UnifiedEntityDAO<?, ?> dao2) {
		// TODO Auto-generated constructor stub
		logger = logger2;
		diskCache = diskCache2;
		dao = dao2;
	}
}
