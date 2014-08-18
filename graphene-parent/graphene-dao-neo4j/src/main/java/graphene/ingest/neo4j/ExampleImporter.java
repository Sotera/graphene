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
	private BatchInserter db;
	private BatchInserterIndexProvider lucene;
	private Config config = new Config(null);

	private Map<String, String> getDefaultParams() {
		Map<String, String> params = new HashMap<>();
		params.put("neostore.nodestore.db.mapped_memory", "20M");
		params.put("neostore.propertystore.db.mapped_memory", "90M");
		params.put("neostore.propertystore.db.index.mapped_memory", "1M");
		params.put("neostore.propertystore.db.index.keys.mapped_memory", "1M");
		params.put("neostore.propertystore.db.strings.mapped_memory", "130M");
		params.put("neostore.propertystore.db.arrays.mapped_memory", "130M");
		params.put("neostore.relationshipstore.db.mapped_memory", "50M");
		return params;
	}

	public ExampleImporter(File graphDb) {
		Map<String, String> config = getDefaultParams();

		db = createBatchInserter(graphDb, config);
		lucene = createIndexProvider();
		report = createReport();
	}

	protected StdOutReport createReport() {
		return new StdOutReport(10 * 1000 * 1000, 100);
	}

	protected LuceneBatchInserterIndexProvider createIndexProvider() {
		return new LuceneBatchInserterIndexProvider(db);
	}

	protected BatchInserter createBatchInserter(File graphDb,
			Map<String, String> config) {
		return BatchInserters.inserter(graphDb.getAbsolutePath(), config);
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			System.err
					.println("Usage java -jar batchimport.jar data/dir nodes.csv relationships.csv [node_index node-index-name fulltext|exact nodes_index.csv rel_index rel-index-name fulltext|exact rels_index.csv ....]");
		}
		File graphDb = new File(args[0]);
		File nodesFile = new File(args[1]);
		File relationshipsFile = new File(args[2]);

		if (graphDb.exists()) {
			FileUtils.deleteRecursively(graphDb);
		}
		ExampleImporter importer = new ExampleImporter(graphDb);
		try {
			if (nodesFile.exists()) {
				importer.importNodes(new FileReader(nodesFile));
			} else {
				System.err.println("Nodes file " + nodesFile
						+ " does not exist");
			}

			if (relationshipsFile.exists()) {
				importer.importRelationships(new FileReader(relationshipsFile));
			} else {
				System.err.println("Relationships file " + relationshipsFile
						+ " does not exist");
			}

			for (int i = 3; i < args.length; i = i + 4) {
				String elementType = args[i];
				String indexName = args[i + 1];
				String indexType = args[i + 2];
				String indexFileName = args[i + 3];
				importer.importIndex(elementType, indexName, indexType,
						indexFileName);
			}
		} finally {
			importer.finish();
		}
	}

	void finish() {
		lucene.shutdown();
		db.shutdown();
		report.finish();
	}

	void importNodes(Reader reader) throws IOException {
		BufferedReader bf = new BufferedReader(reader);
		final RowData data = new RowData(bf.readLine(), "\t", 0, config);
		String line;
		long l = 0;
		report.reset();
		while ((line = bf.readLine()) != null) {
			try {
				db.createNode(data.updateMap(line));
				report.dots();
			} catch (Exception e) {
				System.out.println("Error on line " + l + ": " + line);
				e.printStackTrace();
			}
			l++;
		}
		report.finishImport("Nodes");
	}

	void importRelationships(Reader reader) throws IOException {
		BufferedReader bf = new BufferedReader(reader);
		final RowData data = new RowData(bf.readLine(), "\t", 3,config);
		Object[] rel = new Object[3];
		final RelType relType = new RelType();
		String line;
		report.reset();
		long l = 0;
		while ((line = bf.readLine()) != null) {
			try {
				final Map<String, Object> properties = data
						.updateMap(line, rel);
				db.createRelationship(id(rel[0]), id(rel[1]),
						relType.update(rel[2]), properties);
				report.dots();
			} catch (Exception e) {
				System.out.println("Error on line " + l + ": " + line);
				e.printStackTrace();
			}
			l++;

		}
		report.finishImport("Relationships");
	}

	void importIndex(String indexName, BatchInserterIndex index, Reader reader)
			throws IOException {

		BufferedReader bf = new BufferedReader(reader);

		final RowData data = new RowData(bf.readLine(), "\t", 1,config);
		Object[] node = new Object[1];
		String line = null;
		report.reset();
		long l = 0;

		while ((line = bf.readLine()) != null) {
			try {
				final Map<String, Object> properties = data.updateMap(line,
						node);
				index.add(id(node[0]), properties);
				report.dots();
			} catch (Exception e) {
				System.out.println("Error on line " + l + ": " + line);
				e.printStackTrace();
			}
			l++;
		}

		report.finishImport("Done inserting into " + indexName + " Index");
	}

	private BatchInserterIndex nodeIndexFor(String indexName, String indexType) {
		return lucene.nodeIndex(indexName, configFor(indexType));
	}

	private BatchInserterIndex relationshipIndexFor(String indexName,
			String indexType) {
		return lucene.relationshipIndex(indexName, configFor(indexType));
	}

	private Map<String, String> configFor(String indexType) {
		return indexType.equals("fulltext") ? FULLTEXT_CONFIG : EXACT_CONFIG;
	}

	private long id(Object id) {
		return FastNumberUtils.parseLongWithCheck(id.toString());
	}

	private void importIndex(String elementType, String indexName,
			String indexType, String indexFileName) throws IOException {
		File indexFile = new File(indexFileName);
		if (!indexFile.exists()) {
			System.err.println("Index file " + indexFile + " does not exist");
			return;
		}
		BatchInserterIndex index = elementType.equals("node_index") ? nodeIndexFor(
				indexName, indexType) : relationshipIndexFor(indexName,
				indexType);
		importIndex(indexName, index, new FileReader(indexFile));
	}
}