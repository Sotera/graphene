package graphene.ingest;

import graphene.dao.UnifiedCommunicationEventDAO;
import graphene.util.fs.DiskCache;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * This class acts as an iterator for a table, and, if possible, uses a local
 * disk copy to avoid hitting the [remote] database.
 * 
 * @author djue
 * 
 * @param <UnifiedCommunicationEvent100>
 */
public class UnifiedCommunicationEventIterator<UnifiedCommunicationEvent101> {
	private static final String FILE_NAME = "T:/data/comEvents.data";
	private static final long MAX_RESULTS = -1;
	private UnifiedCommunicationEventDAO dao;

	@Inject
	private DiskCache<?> diskCache;

	@Inject
	private Logger logger;

	public UnifiedCommunicationEventIterator() {
		// TODO Auto-generated constructor stub
	}

	public UnifiedCommunicationEventIterator(Logger logger2,
			DiskCache<?> diskCache2, UnifiedCommunicationEventDAO<?, ?> dao2) {
		// TODO Auto-generated constructor stub
		logger = logger2;
		diskCache = diskCache2;
		dao = dao2;
	}

}
