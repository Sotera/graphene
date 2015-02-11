package graphene.ingest.neo4j;

import static org.neo4j.index.impl.lucene.LuceneIndexImplementation.EXACT_CONFIG;
import static org.neo4j.index.impl.lucene.LuceneIndexImplementation.FULLTEXT_CONFIG;
import graphene.util.FastNumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.batchimport.Report;
import org.neo4j.batchimport.StdOutReport;
import org.neo4j.batchimport.importer.RelType;
import org.neo4j.batchimport.importer.RowData;
import org.neo4j.batchimport.utils.Config;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a generic version of the batch importer that came with Neo4J (off
 * their github repo, and slightly modified to compile here.)
 * 
 * We don't use this class, it's here for reference to build dedicated
 * importers.
 * 
 * @author djue
 * 
 */
public class ExampleImporter {
	private static Report report;

	public static void main(final String[] args) throws IOException {
		if (args.length < 3) {
			System.err
					.println("Usage java -jar batchimport.jar data/dir nodes.csv relationships.csv [node_index node-index-name fulltext|exact nodes_index.csv rel_index rel-index-name fulltext|exact rels_index.csv ....]");
		}
		final File graphDb = new File(args[0]);
		final File nodesFile = new File(args[1]);
		final File relationshipsFile = new File(args[2]);

		if (graphDb.exists()) {
			FileUtils.deleteRecursively(graphDb);
		}
		final ExampleImporter importer = new ExampleImporter(graphDb);
		try {
			if (nodesFile.exists()) {
				importer.importNodes(new FileReader(nodesFile));
			} else {
				System.err.println("Nodes file " + nodesFile + " does not exist");
			}

			if (relationshipsFile.exists()) {
				importer.importRelationships(new FileReader(relationshipsFile));
			} else {
				System.err.println("Relationships file " + relationshipsFile + " does not exist");
			}

			for (int i = 3; i < args.length; i = i + 4) {
				final String elementType = args[i];
				final String indexName = args[i + 1];
				final String indexType = args[i + 2];
				final String indexFileName = args[i + 3];
				importer.importIndex(elementType, indexName, indexType, indexFileName);
			}
		} finally {
			importer.finish();
		}
	}

	private final BatchInserter db;
	private final BatchInserterIndexProvider lucene;

	private final Config config = new Config(null);

	private static Logger logger = LoggerFactory.getLogger(ExampleImporter.class);

	public ExampleImporter(final File graphDb) {
		final Map<String, String> config = getDefaultParams();

		db = createBatchInserter(graphDb, config);
		lucene = createIndexProvider();
		report = createReport();
	}

	private Map<String, String> configFor(final String indexType) {
		return indexType.equals("fulltext") ? FULLTEXT_CONFIG : EXACT_CONFIG;
	}

	protected BatchInserter createBatchInserter(final File graphDb, final Map<String, String> config) {
		return BatchInserters.inserter(graphDb.getAbsolutePath(), config);
	}

	protected LuceneBatchInserterIndexProvider createIndexProvider() {
		return new LuceneBatchInserterIndexProvider(db);
	}

	protected StdOutReport createReport() {
		return new StdOutReport(10 * 1000 * 1000, 100);
	}

	void finish() {
		lucene.shutdown();
		db.shutdown();
		report.finish();
	}

	private Map<String, String> getDefaultParams() {
		final Map<String, String> params = new HashMap<>();
		params.put("neostore.nodestore.db.mapped_memory", "20M");
		params.put("neostore.propertystore.db.mapped_memory", "90M");
		params.put("neostore.propertystore.db.index.mapped_memory", "1M");
		params.put("neostore.propertystore.db.index.keys.mapped_memory", "1M");
		params.put("neostore.propertystore.db.strings.mapped_memory", "130M");
		params.put("neostore.propertystore.db.arrays.mapped_memory", "130M");
		params.put("neostore.relationshipstore.db.mapped_memory", "50M");
		return params;
	}

	private long id(final Object id) {
		return FastNumberUtils.parseLongWithCheck(id.toString());
	}

	void importIndex(final String indexName, final BatchInserterIndex index, final Reader reader) throws IOException {

		final BufferedReader bf = new BufferedReader(reader);

		final RowData data = new RowData(bf.readLine(), "\t", 1, config);
		final Object[] node = new Object[1];
		String line = null;
		report.reset();
		long l = 0;

		while ((line = bf.readLine()) != null) {
			try {
				final Map<String, Object> properties = data.updateMap(line, node);
				index.add(id(node[0]), properties);
				report.dots();
			} catch (final Exception e) {
				System.out.println("Error on line " + l + ": " + line);
				logger.error(e.getMessage());
			}
			l++;
		}

		report.finishImport("Done inserting into " + indexName + " Index");
	}

	private void importIndex(final String elementType, final String indexName, final String indexType,
			final String indexFileName) throws IOException {
		final File indexFile = new File(indexFileName);
		if (!indexFile.exists()) {
			System.err.println("Index file " + indexFile + " does not exist");
			return;
		}
		final BatchInserterIndex index = elementType.equals("node_index") ? nodeIndexFor(indexName, indexType)
				: relationshipIndexFor(indexName, indexType);
		importIndex(indexName, index, new FileReader(indexFile));
	}

	void importNodes(final Reader reader) throws IOException {
		final BufferedReader bf = new BufferedReader(reader);
		final RowData data = new RowData(bf.readLine(), "\t", 0, config);
		String line;
		long l = 0;
		report.reset();
		while ((line = bf.readLine()) != null) {
			try {
				db.createNode(data.updateMap(line));
				report.dots();
			} catch (final Exception e) {
				System.out.println("Error on line " + l + ": " + line);
				logger.error(e.getMessage());
			}
			l++;
		}
		report.finishImport("Nodes");
	}

	void importRelationships(final Reader reader) throws IOException {
		final BufferedReader bf = new BufferedReader(reader);
		final RowData data = new RowData(bf.readLine(), "\t", 3, config);
		final Object[] rel = new Object[3];
		final RelType relType = new RelType();
		String line;
		report.reset();
		long l = 0;
		while ((line = bf.readLine()) != null) {
			try {
				final Map<String, Object> properties = data.updateMap(line, rel);
				db.createRelationship(id(rel[0]), id(rel[1]), relType.update(rel[2]), properties);
				report.dots();
			} catch (final Exception e) {
				System.out.println("Error on line " + l + ": " + line);
				logger.error(e.getMessage());
			}
			l++;

		}
		report.finishImport("Relationships");
	}

	private BatchInserterIndex nodeIndexFor(final String indexName, final String indexType) {
		return lucene.nodeIndex(indexName, configFor(indexType));
	}

	private BatchInserterIndex relationshipIndexFor(final String indexName, final String indexType) {
		return lucene.relationshipIndex(indexName, configFor(indexType));
	}
}