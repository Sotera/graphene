package graphene.dao.neo4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.batch.CypherResult;

public class CypherRestShellTest {
	public static void main(String[] args) throws IOException {
		String uri = (args.length > 0) ? args[0]
				: "http://localhost:7474/db/data";
		RestAPIFacade restAPIFacade = args.length > 1 ? new RestAPIFacade(uri,
				args[1], args[2]) : new RestAPIFacade(uri);
		System.out.println("Connected to " + uri);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String query;
			System.out.print("Query: ");
			while ((query = reader.readLine()) != null && !query.isEmpty()) {
				long time = System.currentTimeMillis();
				CypherResult result = restAPIFacade.query(query, null);
				time = System.currentTimeMillis() - time;
				System.out.println(result.getColumns());
				
				int numRows = 0;
				for (List<Object> row : result.getData()) {
					System.out.println(row);
					numRows++;
				}
				
				System.out.println(numRows + " row(s), roundtrip time " + time
						+ " ms.");
				System.out.print("Query: ");
			}
		} finally {
			restAPIFacade.close();
		}
	}
}
