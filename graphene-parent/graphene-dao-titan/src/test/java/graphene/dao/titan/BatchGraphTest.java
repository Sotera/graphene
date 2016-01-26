/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.dao.titan;

import java.util.Iterator;
import java.util.Random;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.VertexIDType;

public class BatchGraphTest {
	public static Logger logger = LoggerFactory
			.getLogger(RexterConnectionTest.class);
	public static final int MAXNODES = 1000;
	public static final Random rand = new Random();

	public static void main(String[] args) {
		Configuration conf = new BaseConfiguration();
		conf.setProperty("storage.backend", "cassandrathrift");
		conf.setProperty("storage.hostname", "127.0.0.1");
		//conf.setProperty("storage.port", 9160);
		TitanGraph g = TitanFactory.open(conf);
		BatchGraph<TitanGraph> bgraph = new BatchGraph(g, VertexIDType.STRING,
				1000);
		// for (String[] quad : quads) {
		// Vertex[] vertices = new Vertex[2];
		// for (int i=0;i<2;i++) {
		// vertices[i] = bgraph.getVertex(quad[i]);
		// if (vertices[i]==null) vertices[i]=bgraph.addVertex(quad[i]);
		// }
		// Edge edge = bgraph.addEdge(null,vertices[0],vertices[1],quad[2]);
		// edge.setProperty("annotation",quad[3]);
		// }

		for (int k = 0; k < MAXNODES; k++) {
			Vertex[] vertices = new Vertex[2];

			for (int i = 0; i < 2; i++) {
				String id = "Vertex-" + rand.nextInt(MAXNODES);
				vertices[i] = bgraph.getVertex(id);
				if (vertices[i] == null) {
					vertices[i] = bgraph.addVertex(id);
					vertices[i].setProperty("label", id);
				}
			}
			String edgeId = "Edge-" + vertices[0].getId() + "--"
					+ vertices[1].getId();
			Edge e = bgraph.getEdge(edgeId);
			if (e == null) {
				e = bgraph.addEdge(null, vertices[0], vertices[1], edgeId);
				DateTime dt = new DateTime();
				e.setProperty("edgeData", dt.getMillis());
			}
		}
		bgraph.commit();
		Iterator<Vertex> iter = bgraph.getVertices().iterator();
		logger.debug("Start iterating over vertices");
		while (iter.hasNext()) {
			Vertex v = iter.next();
			Iterator<Edge> eIter = v.getEdges(Direction.BOTH).iterator();
			while (eIter.hasNext()) {
				Edge currentEdge = eIter.next();
				String data = currentEdge.getProperty("edgeData");
				logger.debug("Observing edge " + currentEdge.getLabel());
				logger.debug("has edge with data: "
						+ data
						+ " from "
						+ currentEdge.getVertex(Direction.IN).getProperty(
								"label")
						+ " to "
						+ currentEdge.getVertex(Direction.OUT).getProperty(
								"label"));
			}
		}
		logger.debug("Done iterating over vertices");
	}
}
