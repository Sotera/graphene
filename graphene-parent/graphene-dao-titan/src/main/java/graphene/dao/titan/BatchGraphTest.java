package graphene.dao.titan;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.VertexIDType;

public class BatchGraphTest {
	public static void main() {
		Configuration conf = new BaseConfiguration();
		conf.setProperty("storage.backend", "cassandrathrift");
		conf.setProperty("storage.hostname", "127.0.0.1");
		conf.setProperty("storage.port", 9160);
		TitanGraph g = TitanFactory.open(conf);
		BatchGraph bgraph = new BatchGraph(g, VertexIDType.STRING, 1000);
		// for (String[] quad : quads) {
		// Vertex[] vertices = new Vertex[2];
		// for (int i=0;i<2;i++) {
		// vertices[i] = bgraph.getVertex(quad[i]);
		// if (vertices[i]==null) vertices[i]=bgraph.addVertex(quad[i]);
		// }
		// Edge edge = bgraph.addEdge(null,vertices[0],vertices[1],quad[2]);
		// edge.setProperty("annotation",quad[3]);
		// }

		for (int k = 0; k < 1000; k++) {
			Vertex[] vertices = new Vertex[2];
			for (int i = 0; i < 2; i++) {
				vertices[i] = bgraph.getVertex("Vertex-" + i);
				if (vertices[i] == null)
					vertices[i] = bgraph.addVertex("Vertex-" + i);
			}
			Edge edge = bgraph.addEdge(null, vertices[0], vertices[1], "Edge-"
					+ vertices[0].getId() + "--" + vertices[1].getId());
			DateTime dt = new DateTime();
			edge.setProperty("edgeData",dt.getMillis());
		}
	}
}
