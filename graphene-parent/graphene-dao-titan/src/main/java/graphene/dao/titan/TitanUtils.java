package graphene.dao.titan;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.util.TitanCleanup;

public class TitanUtils {
	/**
	 * Clear the database (i.e. remove all nodes and edges)
	 * 
	 * @param g
	 */
	public static void clear(TitanGraph g) {
		TitanCleanup.clear(g);
	
	}
}
