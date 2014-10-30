package graphene.dao.titan;

import java.io.IOException;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph;

public interface BlueprintImporter {
	public void CreateSchema(TitanGraph g);

	public void Import(final TransactionalGraph g) throws IOException;

}
