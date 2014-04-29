package graphene.model.memorydb;

import graphene.dao.DataSourceListDAO;

import graphene.util.jvm.JVMHelper;
import graphene.util.stats.TimeReporter;

import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * For the time being, this singleton is not being used like it was in 3.x
 * 
 * Changes:
 * 
 * This class does not initialize memdb anymore. MemDb is initialized on startup
 * in the {@link DAOModule}, if settings indicate it should be enabled.
 * 
 * This class has many references to the settings for Indexer classes, which are
 * not being used in 3.x
 * 
 * Most of this class serves as a legacy property holder. This class may be
 * revived at a later date, but for now it should not be instantiated or loaded.
 * 
 * Depending on the settings in the context (web.xml), this class loads some or
 * all of the unique database identifier values into an index
 * 
 * @author PWG for DARPA
 * 
 */

public class PreLoaderImpl implements PreLoader {
	boolean isLoaded = false;
	// The following apply to loading an index only. We now load the whole
	// table into a memory database

	boolean loadAll = false; 
	boolean indexAll = false;
	boolean loadTypeNames = false; // We no longer have a Types table.
	boolean indexNames = false;
	boolean indexAddresses = false;
	boolean indexPhones = false;
	int maxEntries = 1000; // for debugging

	// Following loads the database into RAM
	boolean useMemDB = true;

	// Following is a flag to bypass the DB connection check during
	// initialization in testHibernate
	// not needed boolean bypassTestHibernate = false;

	@Inject
	private Logger logger;

	@Inject
	private DataSourceListDAO datasourceDao;

	@Inject
	private IMemoryDB memDb;

	private Map<String, String> settings;

	public PreLoaderImpl(Map<String, String> settings) {

		// logger.trace("PreLoaderImpl: EntityRefDAO == null ? "
		// + (db == null ? "true" : "false"));
		// logger.trace("PreLoaderImpl: indexer == null ? "
		// + (i == null ? "true" : "false"));
		// this.db = db;
		// this.datasourceDao = datasourceDao;
		// this.datasourceDao.getList(); // initialize it
		// this.indexer = i;
		// this.memDb = memDb;
		this.settings = settings;

		// initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#initialize()
	 */
	@Override
	public void initialize() {
		indexNames = (settings.get("indexNames").equalsIgnoreCase("true"));
		indexAddresses = (settings.get("indexAddresses")
				.equalsIgnoreCase("true"));
		indexPhones = (settings.get("indexPhones").equalsIgnoreCase("true"));
		indexAll = (settings.get("indexAll").equalsIgnoreCase("true"));
		maxEntries = Integer.parseInt(settings.get("maxIndexRecords"));

		useMemDB = true; // overridden below if property found in web.xml as
							// substituted by Maven
		String useMem = settings.get("useMemDB");
		if (useMem.equalsIgnoreCase("true")) {
			logger.info("Using preloader with in memory database.");
			useMemDB = true;
		} else {
			logger.info("Using preloader with traditional database.");
			useMemDB = false;
		}
		// above handles case where Eclipse is not seeing the Maven property
		// substitution.
		// if it isn't found (useMem would start with $) we use the default
		// setting.

		// db.setEntityRefAccessType(useMemDB);

		
		// DEBUG
		logger.debug("PreLoaderImpl: useMemDB = " + useMemDB);
		datasourceDao.getList(); // initialize it

		logger.trace("PreLoader initialize");

		TimeReporter t = new TimeReporter("Preloader initialize", logger);
		logger.trace("Free memory before preload " + JVMHelper.getFreeMem());

		if (useMemDB) {
			memDb.initialize(maxEntries);
		} else {
			logger.trace("Did not preload as no methods specified");
		}

		logger.trace("Free memory after preload: " + JVMHelper.getFreeMem());

		t.logAsCompleted();
		logger.trace("Finished preload");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#isLoaded()
	 */
	@Override
	public boolean isLoaded() {
		// TODO Auto-generated method stub
		return isLoaded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#setLoadAll(boolean)
	 */
	@Override
	public void setLoadAll(boolean loadAll) {
		this.loadAll = loadAll;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grapheneweb.services.database.PreLoader#setLoadTypeNames(boolean)
	 */
	@Override
	public void setLoadTypeNames(boolean load) {
		this.loadTypeNames = load;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#setindexAll(boolean)
	 */
	@Override
	public void setindexAll(boolean index) {
		this.indexAll = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#setIndexNames(boolean)
	 */
	@Override
	public void setIndexNames(boolean loadNames) {
		this.indexNames = loadNames;
	}

	// for Spring
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grapheneweb.services.database.PreLoader#setIndexAddresses(boolean)
	 */
	@Override
	public void setIndexAddresses(boolean load) {
		this.indexAddresses = load;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#isIndexPhones()
	 */
	@Override
	public boolean isIndexPhones() {
		return indexPhones;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grapheneweb.services.database.PreLoader#setIndexPhones(boolean)
	 */
	@Override
	public void setIndexPhones(boolean indexPhones) {
		this.indexPhones = indexPhones;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#getMaxEntries()
	 */
	@Override
	public long getMaxEntries() {
		return maxEntries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grapheneweb.services.database.PreLoader#setMaxEntries(int)
	 */
	@Override
	public void setMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
	}


}
