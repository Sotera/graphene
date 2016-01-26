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

package graphene.rest.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;

import org.apache.tapestry5.annotations.Log;

@Path("/csgraph/")
@Produces("application/json")
public interface CSGraphServerRS {

	/**
	 * REST service to return a property-type graph.
	 * 
	 * @param type
	 * @param value
	 * @param degree
	 * @param maxNodes
	 * @param maxEdgesPerNode
	 * @param bipartite
	 * @param leafNodes
	 * @param showNameNodes
	 * @param showIcons
	 * @return
	 */
	@Log
	@GET
	@Path("/{type}/{value}")
	@Produces("application/json")
	public abstract V_CSGraph getProperties(@PathParam("type") String type, @PathParam("value") String[] value,
			@QueryParam("degree") String degree, @QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode, @QueryParam("bipartite") boolean bipartite,
			@QueryParam("showLeafNodes") boolean leafNodes, @QueryParam("showNameNodes") boolean showNameNodes,
			@QueryParam("showIcons") boolean showIcons,
			@QueryParam("useSaved") @DefaultValue(value = "true") boolean useSaved);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save")
	@Produces("text/plain")
	Response saveGraph(@QueryParam("seed") @DefaultValue(value = "unknown") String graphSeed,
			@QueryParam("username") @DefaultValue(value = "unknown") String username,
			@QueryParam("timeStamp") @DefaultValue(value = "0") String timeStamp, String graph);
}