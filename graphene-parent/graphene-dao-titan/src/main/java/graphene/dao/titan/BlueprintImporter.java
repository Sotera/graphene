package graphene.dao.titan;

import java.io.IOException;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;

public interface BlueprintImporter {
		public void CreateSchema(TitanGraph g);
		public void Import(BatchGraph<TransactionalGraph> g) throws IOException;
}
