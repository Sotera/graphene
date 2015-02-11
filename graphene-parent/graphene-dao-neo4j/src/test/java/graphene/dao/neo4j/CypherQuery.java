package graphene.dao.neo4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CypherQuery {
	private static final Logger logger = LoggerFactory.getLogger(CypherQuery.class);

	public static void main(final String[] args) {
		try {
			// System.out.println("starting test");
			// final RestAPI api = new RestAPIFacade(
			// "http://localhost:7474/db/data/");
			// System.out.println("API created");
			// final RestCypherQueryEngine engine = new
			// RestCypherQueryEngine(api);
			// System.out.println("engine created");
			// Map<String, Object> m = new HashMap<String, Object>();
			// m.put("id", 13);
			// final QueryResult<Map<String, Object>> result = engine
			// .query("START n=node(10) MATCH (n)<-[:HasAttribute]->(x) RETURN x.Identifier",
			// m);
			//
			// System.out.println("query created");
			// for (Map<String, Object> row : result) {
			// System.out.println("row is " + row);
			// // long id=((Number)row.get("id")).longValue();
			// // System.out.println("id is " + id);
			// }
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
	}
}
