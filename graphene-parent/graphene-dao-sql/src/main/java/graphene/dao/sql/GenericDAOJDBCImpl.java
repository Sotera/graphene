package graphene.dao.sql;

import graphene.model.idl.G_CallBack;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.util.db.MainDB;
import graphene.util.jvm.JVMHelper;
import graphene.util.stats.MemoryReporter;
import graphene.util.stats.TimeReporter;
import graphene.util.validator.ValidationUtils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nonnegative;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLServer2005Templates;
import com.mysema.query.sql.SQLServer2012Templates;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.mysql.MySQLQuery;
import com.mysema.query.types.EntityPath;

public abstract class GenericDAOJDBCImpl<T> implements G_DataAccess {

	/**
	 * If you need to change the database that is used, set it in the
	 * constructor of your implementation. Otherwise it gets the default
	 * connection pool that was marked with a MainDB annotation.
	 * 
	 * TODO: Change this to force the end user to supply the type of connection
	 * pool from their DAO module. That will have the least tight
	 * coupling.--djue
	 */
	@Inject
	@MainDB
	private DBConnectionPoolService cps;

	@Inject
	protected Logger logger;

	private boolean ready;

	/**
	 * A non throttling version of a callback performer. Need to test this with
	 * large datasets
	 * 
	 * <BR>
	 * 
	 * @param initialOffset
	 * @param maxResults
	 * @param cb
	 * @param q
	 * @return
	 */
	public boolean basicCallback(long initialOffset, final long maxResults, final G_CallBack cb, final G_EntityQuery q) {
		if (initialOffset == 0) {
			// For SQL offsets, it is one based.
			initialOffset = 1;
		}
		final long offset = initialOffset;
		long numProcessed = 0;
		logger.debug("Performing basic callback performer");
		final TimeReporter t = new TimeReporter("Reading from database...", logger);
		final MemoryReporter m = new MemoryReporter("Reading from database...", logger);
		boolean doneProcessing = false;
		while (!doneProcessing && ((maxResults < 0) || (numProcessed <= maxResults))) {

			G_SearchResults results = null;
			try {
				if (q == null) {
					// There was no query object, which mean just get
					// everything.

					results = getAll(offset, maxResults);
				} else {
					// We have some sort of query object.
					// XXX: We may need to replace Q with ? extends Generic
					// Query Object, so we have access to setting limits and
					// offset.
					// results = findByQuery(offset, maxResults, q);
					q.setFirstResult(offset);
					q.setMaxResult(maxResults);
					results = search(q);
				}
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
			if ((results == null) || (results.getResults().size() == 0)) {

				/*
				 * This should be ok, as long as the dao query is not requesting
				 * chunks based on a data value. i.e. there may be no results
				 * between x and x', leading to premature termination of the
				 * method.
				 */
				doneProcessing = true;
			} else {
				/*
				 * Deal with any references to results.size(), so GC can clean
				 * up ASAP
				 */

				numProcessed += results.getResults().size();
				// Execute callbacks
				for (G_SearchResult p : results.getResults()) {
					try {
						if (!cb.execute(p, q)) {
							logger.error("Fatal error in callback from loading index");
							break;
						}
					} catch (final AvroRemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					p = null;
				}
				results = null;

				/*
				 * First, address memory concerns. Then Throttle down if
				 * performance was hindered. Otherwise throttle up.
				 */
				m.reportMegabytesUsed();
				if (m.getFreeMegabytes() < 200) {
					// Adjust for memory concerns
					JVMHelper.suggestGC();

				}
			}
		}
		logger.debug("Processed " + numProcessed + " rows in " + t.report());
		return true;
	}

	/**
	 * 
	 * @param conn
	 * @param o
	 * @return
	 * @throws SQLException
	 */
	protected SQLQuery from(final Connection conn, final EntityPath<?>... o) {
		final SQLTemplates dialect = new SQLServer2005Templates(); // SQL-dialect
		return new SQLQuery(conn, dialect).from(o);
	}

	/**
	 * 
	 * @param conn
	 *            a connection, probably for a connection pool.
	 * @param oldSchema
	 *            The string of the old schema you want to replace
	 * @param newSchema
	 *            The string of the new schema you want to use
	 * @param o
	 *            One or more instances of query classes (that start with Q)
	 * @return an SQLQuery object that you can start adding constraints to.
	 */
	protected SQLQuery from(final Connection conn, final String oldSchema, final String newSchema,
			final EntityPath<?>... o) {
		// Tell QueryDSL to print the schema
		final SQLTemplates template = SQLServer2012Templates.builder().printSchema().build();
		final Configuration c = new Configuration(template);
		// override the default schema for the entity path, usually dbo
		c.registerSchemaOverride(oldSchema, newSchema);
		return new SQLQuery(conn, c).from(o);
	}

	/**
	 * Use this method to grab a connection from the pool. It's best to hold
	 * onto the connection in a local variable so you can close it manually
	 * after your call. Otherwise you may encounter slow downs due to
	 * connections not being returned to the pool appropriately.
	 * 
	 * @return a connection used for creating an SQL call.
	 * @throws Exception
	 */
	protected Connection getConnection() throws Exception {
		if (cps == null) {

			throw new Exception(
					"DBConnectionPoolService was not injected properly, check your bindings to make sure the registry knows how to supply this class.  Are you sure you didn't try to call new on the implementation?");
		}
		if (!cps.isInitialized()) {
			cps.init();
		}
		return cps.getConnection();
	}

	/**
	 * @return the cps
	 */
	public final DBConnectionPoolService getCps() {
		return cps;
	}

	/**
	 * @return the logger
	 */
	public final Logger getLogger() {
		return logger;
	}

	/**
	 * The default implementation of this relies solely on the isReady() being
	 * true.
	 */
	@Override
	public double getReadiness() {
		return isReady() ? 1.0d : 0;
	}

	@Override
	public boolean isReady() {
		if ((cps != null) && cps.isInitialized()) {
			ready = true;
		} else {
			ready = false;
		}
		return ready;
	}

	/**
	 * This is now a default, but it is intended that implementaitons will
	 * override and use a different callback if they want to.
	 */
	@Override
	public boolean performCallback(final long offset, final long maxResults, final G_CallBack cb, final G_EntityQuery q) {
		return basicCallback(offset, maxResults, cb, q);
	}

	@Override
	public String saveObject(final Object g, final String id, final String indexName, final String type,
			final boolean useDelay) {
		return null;
	}

	/**
	 * @param cps
	 *            the cps to set
	 */
	public final void setCps(final DBConnectionPoolService cps) {
		this.cps = cps;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	public final void setLogger(final Logger logger) {
		this.logger = logger;
	}

	/**
	 * 
	 * @param q
	 * @param sq
	 * @return a modified SQLQuery object
	 */
	protected SQLQuery setOffsetAndLimit(final G_EntityQuery q, SQLQuery sq) {
		if (ValidationUtils.isValid(q)) {
			if (ValidationUtils.isValid(q.getFirstResult())) {
				sq = sq.offset(q.getFirstResult());
			}
			if (ValidationUtils.isValid(q.getMaxResult())) {
				sq = sq.limit(q.getMaxResult());
			}
		}
		return sq;
	}

	/**
	 * 
	 * @param q
	 * @param sq
	 * @param defaultLimit
	 * @return a modified SQLQuery object
	 */
	protected SQLQuery setOffsetAndLimit(final G_EntityQuery q, SQLQuery sq, final long defaultLimit) {
		if (ValidationUtils.isValid(q)) {
			if (ValidationUtils.isValid(q.getFirstResult()) && (defaultLimit > q.getFirstResult())) {
				sq = sq.offset(q.getFirstResult());
			}
			if (ValidationUtils.isValid(q.getMaxResult())) {
				sq = sq.limit(q.getMaxResult());
			} else {
				sq = sq.limit(defaultLimit);
			}
		}
		return sq;
	}

	protected MySQLQuery setOffsetAndLimit(@Nonnegative final long offset, @Nonnegative final long limit, MySQLQuery sq) {
		if ((limit > 0) && (offset == limit)) {
			logger.error("Offset and Limit were non zero and equal in query, so query will not be executed: "
					+ sq.toString());
			sq = null;
		} else {
			if (ValidationUtils.isValid(offset)) {
				sq = sq.offset(offset);
			}
			if (ValidationUtils.isValid(limit)) {
				sq = sq.limit(limit);
			}
		}
		return sq;
	}

	/**
	 * A safe way of adding offset and limit, directly using long values.
	 * 
	 * 
	 * @param offset
	 * @param limit
	 * @param sq
	 * @return a modified SQLQuery object
	 */
	protected SQLQuery setOffsetAndLimit(@Nonnegative final long offset, @Nonnegative final long limit, SQLQuery sq) {
		if ((limit > 0) && (offset == limit)) {
			logger.error("Offset and Limit were non zero and equal in query, so query will not be executed: "
					+ sq.toString());
			sq = null;
		} else {
			if (ValidationUtils.isValid(offset)) {
				sq = sq.offset(offset);
			}
			if (ValidationUtils.isValid(limit)) {
				sq = sq.limit(limit);
			}
		}
		return sq;
	}

	@Override
	public Void setReady(final boolean b) {
		this.ready = b;
		return null;
	}

	/**
	 * This is a safe way of adding the offset and limit. We can encapsulate
	 * validation and error correction here to prevent duplicate code and reduce
	 * maintenance.
	 * 
	 * XXX: This is not quite what we want, maybe. For throttling callbacks we
	 * would have to modify Q each time for the offset, but we don't want any
	 * side effects from modifying the original query.
	 * 
	 * @param q
	 * @param sq
	 * @return
	 */
	// protected SQLQuery setOffsetAndLimit(BasicQuery q, SQLQuery sq) {
	// return setOffsetAndLimit(q.getFirstResult(), q.getMaxResult(), sq);
	// }

	/**
	 * calls a throttlingCallback with initialChunkSize 25000, minChunkSize 10
	 * and maxChunkSize 250000
	 * 
	 * @param initialOffset
	 * @param maxResults
	 * @param cb
	 * @param q
	 * @return true if successful, false otherwise.
	 */
	public boolean throttlingCallback(final long initialOffset, final long maxResults, final G_CallBack cb,
			final G_EntityQuery q) {
		// chunkSize = 25000, minChunkSize = 10, maxChunkSize = 250000
		return throttlingCallback(initialOffset, maxResults, cb, q, 25000, 10, 250000);
	}

	/**
	 * The purpose of the throttling code is to provide dynamic balance between
	 * request computation(db side), data transmission (network), and processing
	 * time (local). The 'sweet spot' for fetch size changes depending on the
	 * size of the objects, database load, network speed, and the amount of
	 * processing done in the callbacks. Each of these variables can change
	 * during the course of the execution.
	 * 
	 * The code as provided tries to increase fetch size as long as it doesn't
	 * hurt performance. A small growth rate allows the code to spend more time
	 * making requests near the sweetspot. However the growth rate needs to be
	 * large enough to react to changes in throughput.
	 * 
	 * A well chosen initial chunkSize increases the 'attack'.
	 * 
	 * 
	 * @param initialOffset
	 * @param maxResults
	 *            if set to 0, will load everything.
	 * @param cb
	 *            The callback to be performed on each result of the query.
	 * @param q
	 *            A query object. If null, this method will call the getAll()
	 *            method. Otherwise it will use findByQuery()
	 * @param initialChunkSize
	 * @param minChunkSize
	 * @param maxChunkSize
	 * @return
	 */
	public boolean throttlingCallback(long initialOffset, final long maxResults, final G_CallBack cb,
			final G_EntityQuery q, final long initialChunkSize, final long minChunkSize, final long maxChunkSize) {
		logger.debug("Performing throttling callback performer");
		logger.debug("initialOffset = " + initialOffset);
		logger.debug("maxResults = " + maxResults);
		logger.debug("initialChunkSize = " + initialChunkSize);
		logger.debug("minChunkSize = " + minChunkSize);
		logger.debug("maxChunkSize = " + maxChunkSize);
		if (initialOffset == 0) {
			// For SQL offsets, it is one based.
			initialOffset = 1;
		}
		long offset = initialOffset;
		long chunkSize = ((maxResults > 0) && (maxResults < initialChunkSize) ? maxResults : initialChunkSize);
		long numProcessed = 0;
		long bestFetchSize = 0;
		final double fetchSizeGrowthRate = 1.05;
		// used to determine throttling
		final double fuzziness = 1.05;
		double previousRate = 0.0, bestRate = 0.0;
		int zeroResultsEvent = 0;
		final int maxZeroResultsEvents = 1;
		final TimeReporter t = new TimeReporter("Reading from database...", logger);
		final MemoryReporter m = new MemoryReporter("Reading from database...", logger);
		boolean doneProcessing = false;
		while (!doneProcessing && ((maxResults <= 0) || (numProcessed <= maxResults))) {

			final long startTime = System.currentTimeMillis();
			// double check the chunkSize against the total number of rows.
			if ((maxResults > 0) && ((numProcessed + chunkSize) > maxResults)) {
				chunkSize = maxResults - numProcessed;
			}
			G_SearchResults results = null;
			try {
				// if (q == null) {
				// logger.debug("getAll(offset=" + offset + ", chunksize=" +
				// chunkSize);
				// results = search(offset, chunkSize);
				// } else {
				logger.debug("getAll(offset=" + offset + ", chunksize=" + chunkSize + " query=" + q);

				// results = findByQuery(offset, chunkSize, q);
				q.setFirstResult(offset);
				q.setMaxResult(chunkSize);
				results = search(q);
				// }
			} catch (final Exception e) {
				logger.error("Problem in throttling callback: " + e.getMessage());

			}
			if ((results == null) || (results.getResults().size() == 0)) {
				logger.info("Amount of rows processed during this throttling session was zero.");
				/*
				 * This should be ok, as long as the dao query is not requesting
				 * chunks based on a data value. i.e. there may be no results
				 * between x and x', leading to premature termination of the
				 * method.
				 */
				zeroResultsEvent++;
				if (zeroResultsEvent >= maxZeroResultsEvents) {
					doneProcessing = true;
				}
			} else {
				// reset the event count since we encountered something.
				zeroResultsEvent = 0;
			}

			if (!doneProcessing && (results != null)) {
				/*
				 * Deal with any references to results.size(), so GC can clean
				 * up ASAP
				 */

				numProcessed += results.getResults().size();
				final long deltaTime = System.currentTimeMillis() - startTime;
				// rows per time
				final double currentRate = (double) results.getResults().size() / deltaTime;
				logger.trace("Chunk of rows received, processing callback on them.");

				// Execute callbacks
				for (G_SearchResult p : results.getResults()) {
					try {
						if (!cb.execute(p, q)) {
							logger.error("Fatal error in callback from loading index");
							break;
						}
					} catch (final AvroRemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					p = null;
				}

				if (results.getResults().size() < chunkSize) {
					logger.debug("Could not get full chunk - assuming we have all there is");
					doneProcessing = true;
				}

				results = null;

				if ((maxResults > 0) && (numProcessed > maxResults)) {
					logger.error("Processed more than max results, something is wrong.");
					doneProcessing = true;
				} else if (numProcessed == maxResults) {
					logger.debug("Processed up to maxResults" + numProcessed);
					doneProcessing = true;
				}
				// a little logging.
				if ((numProcessed % 1000) == 0) {
					logger.debug("Processed " + numProcessed);
				}
				if (!doneProcessing) {
					// start setting variables up for the next loop.
					// update start
					offset += chunkSize;

					// update rates
					final double deltaRate = currentRate - previousRate;
					logger.debug("Current Rate: " + currentRate + " per ms.  Previous rate:" + previousRate
							+ " per ms. Change: " + deltaRate);

					if (currentRate > bestRate) {
						bestRate = currentRate;
						bestFetchSize = chunkSize;
					}
					logger.debug("Best rate so far: " + bestRate + " at fetch size " + bestFetchSize);
					/*
					 * First, address memory concerns. Then Throttle down if
					 * performance was hindered. Otherwise throttle up.
					 */
					m.reportMegabytesUsed();
					if (m.getFreeMegabytes() < 200) {
						// Adjust for memory concerns
						logger.debug("Throttling down due to memory concerns");
						JVMHelper.suggestGC();
						chunkSize /= fetchSizeGrowthRate;
					} else if ((currentRate * fuzziness) < previousRate) {
						// throttle down
						logger.debug("Throttling down");
						chunkSize /= fetchSizeGrowthRate;
					} else {
						// default is to ask for more and see what happens.
						// throttle up
						logger.debug("Throttling up");
						chunkSize *= fetchSizeGrowthRate;
					}

					/*
					 * Address chunkSizes outside our upper and lower bounds.
					 */
					if (chunkSize < minChunkSize) {
						chunkSize = minChunkSize;
						logger.debug("Transferring at minimum fetch size of " + minChunkSize);
					} else if (chunkSize > maxChunkSize) {
						chunkSize = maxChunkSize;
						logger.debug("Transferring at maximum fetch size of " + maxChunkSize);
					}
					previousRate = currentRate;
				}// end setting up for next chunk
			}
		}
		logger.debug("Processed " + numProcessed + " rows in " + t.report());
		return true;
	}

	/**
	 * This version queries for variable size windows, but uses a supplied min
	 * and max value. This is useful when coordinated with a getAll() or
	 * findByQuery() that uses non contiguous id values, rather than an
	 * incrementing autoid.
	 * 
	 * For instance if the first id is something like 10, and the next id
	 * available is something like 100000000, there is a large gap in between. A
	 * windowing callback would either quit because no results were found in a
	 * window, or it could iterate for an arbitrary amount of empty windows
	 * until it thinks everything is done.
	 * 
	 * By supplying upper and lower bounds to the offset to search for, a
	 * predetermined number of windows can be run, regardless of how many are
	 * empty.
	 * 
	 * Note that you must coordinate the getAll or findByQuery methods in your
	 * implementation to query by an id and not an auto id. (it may work for an
	 * autoid, but that is not what this was designed for.)
	 * 
	 * 
	 * @param initialOffset
	 * @param maxResults
	 * @param cb
	 * @param q
	 * @param initialChunkSize
	 * @param minChunkSize
	 * @param maxChunkSize
	 * @return
	 */
	public boolean throttlingCallbackOnValues(long initialOffset, final long maxResults, final G_CallBack cb,
			final G_EntityQuery q, final long initialChunkSize, final long minChunkSize, final long maxChunkSize,
			final long minValue, final long maxValue) {
		logger.debug("Performing throttling callback performer against values");
		logger.debug("initialOffset = " + initialOffset);
		logger.debug("maxResults = " + maxResults);
		logger.debug("initialChunkSize = " + initialChunkSize);
		logger.debug("minChunkSize = " + minChunkSize);
		logger.debug("maxChunkSize = " + maxChunkSize);
		logger.debug("minValue = " + minValue);
		logger.debug("maxValue = " + maxValue);
		if (initialOffset == 0) {
			// For SQL offsets, it is one based.
			initialOffset = 1;
		}
		long offset = initialOffset;
		// set the chunk size to the lesser of maxResults or initialChunkSize,
		// provided maxResults is valid.
		long chunkSize = ((maxResults > 0) && (maxResults < initialChunkSize) ? maxResults : initialChunkSize);
		long numProcessed = 0;
		long bestFetchSize = 0;
		final double fetchSizeGrowthRate = 1.05;
		// used to determine throttling
		final double fuzziness = 1.05;
		double previousRate = 0.0, bestRate = 0.0;
		final TimeReporter t = new TimeReporter("Reading from database...", logger);
		final MemoryReporter m = new MemoryReporter("Reading from database...", logger);
		boolean doneProcessing = false;

		/*
		 * Loop if we're not done processing, and either one of these: There is
		 * no max result or the number processed so far is less than the max
		 * number of results.
		 */
		while (!doneProcessing && ((maxResults <= 0) || (numProcessed <= maxResults))) {

			final long startTime = System.currentTimeMillis();
			// double check the chunkSize against the total number of rows.
			if ((maxResults > 0) && ((numProcessed + chunkSize) > maxResults)) {
				chunkSize = maxResults - numProcessed;
			}
			G_SearchResults results = null;
			try {
				if (q == null) {
					logger.debug("getAll(offset=" + offset + ", max value=" + (chunkSize + offset) + ")");
					// NOTE that this is different than any others, because we
					// are doing it against value. The limit is not just the
					// chunk size, it's the maximum value for this window.
					results = getAll(offset, chunkSize + offset);
				} else {
					// results = findByQuery(offset, chunkSize, q);
					q.setFirstResult(offset);
					q.setMaxResult(chunkSize);
					results = search(q);
				}
			} catch (final Exception e) {
				logger.error("Problem in throttling callback: " + e.getMessage());

			}
			if (results == null) {
				doneProcessing = true;
			} else if (!doneProcessing) {
				/*
				 * Deal with any references to results.size(), so GC can clean
				 * up ASAP
				 */

				numProcessed += results.getResults().size();
				final long deltaTime = System.currentTimeMillis() - startTime;
				// rows per time
				final double currentRate = (double) results.getResults().size() / deltaTime;
				logger.trace("Chunk of rows received (" + results.getResults().size()
						+ ") , processing callback on them.");

				// Execute callbacks
				for (G_SearchResult p : results.getResults()) {
					try {
						if (!cb.execute(p, q)) {
							logger.error("Fatal error in callback from loading index");
							break;
						}
					} catch (final AvroRemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					p = null;
				}
				results = null;
				if (numProcessed == 0) {
					logger.warn("No results found in this window of " + offset + " to " + (offset + chunkSize));
				}
				if ((maxResults > 0) && (numProcessed > maxResults)) {
					logger.error("Processed more than max results, something is wrong.");
					doneProcessing = true;
				} else if ((maxResults > 0) && (numProcessed == maxResults)) {
					logger.debug("numProcessed == maxResults == " + numProcessed);
					doneProcessing = true;
				} else if ((offset) > maxValue) {
					logger.debug("Processed a chunk that reached the maxValue: " + offset + "<" + maxValue + "<"
							+ (offset + chunkSize));
					doneProcessing = true;
				}

				if (!doneProcessing) {
					// start setting variables up for the next loop.
					// update start
					offset += chunkSize;

					// update rates
					final double deltaRate = currentRate - previousRate;
					logger.debug("Current Rate: " + currentRate + " per ms.  Previous rate:" + previousRate
							+ " per ms. Change: " + deltaRate);

					if (currentRate > bestRate) {
						bestRate = currentRate;
						bestFetchSize = chunkSize;
					}
					logger.debug("Best rate so far: " + bestRate + " at fetch size " + bestFetchSize);
					/*
					 * First, address memory concerns. Then Throttle down if
					 * performance was hindered. Otherwise throttle up.
					 */
					m.reportMegabytesUsed();
					if (m.getFreeMegabytes() < 200) {
						// Adjust for memory concerns
						logger.debug("Throttling down due to memory concerns");
						JVMHelper.suggestGC();
						chunkSize /= fetchSizeGrowthRate;
					} else if ((currentRate * fuzziness) < previousRate) {
						// throttle down
						logger.debug("Throttling down");
						chunkSize /= fetchSizeGrowthRate;
					} else {
						// default is to ask for more and see what happens.
						// throttle up
						logger.debug("Throttling up");
						chunkSize *= fetchSizeGrowthRate;
					}

					/*
					 * Address chunkSizes outside our upper and lower bounds.
					 */
					if (chunkSize < minChunkSize) {
						chunkSize = minChunkSize;
						logger.debug("Transferring at minimum fetch size of " + minChunkSize);
					} else if (chunkSize > maxChunkSize) {
						chunkSize = maxChunkSize;
						logger.debug("Transferring at maximum fetch size of " + maxChunkSize);
					}
					previousRate = currentRate;
				}// end setting up for next chunk
			}
		}
		logger.debug("Processed " + numProcessed + " rows in " + t.report());
		return true;
	}
}