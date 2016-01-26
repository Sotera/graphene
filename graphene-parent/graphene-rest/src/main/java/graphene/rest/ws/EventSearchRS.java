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

import graphene.model.idl.G_TransactionResults;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.tapestry5.annotations.Log;

/**
 * This generalized rest service will eventually replace most other rest
 * services that have a narrow focus.
 * 
 * Two things that must be done for that to happen:
 * 
 * 1) Powerful query structure to handle the types of views we acutally want to
 * show
 * 
 * 2) Ability to serialize the schema definition and pass it back as part of the
 * response (probably using Avro generated types)
 * 
 * @author djue
 * 
 */
@Path("/event")
public interface EventSearchRS {

	/**
	 * Note that accountNumbers and comments are plural, and accept comma
	 * separated values which will be tokenized and searched for.
	 * 
	 * @param account
	 * @param start
	 * @param limit
	 * @param minAmount
	 * @param maxAmount
	 * @param minSecs
	 * @param maxSecs
	 * @param comments
	 * @param sortColumn
	 * @return
	 */
	@Log
	@Produces("application/json")
	@GET
	@Path("/any")
	public abstract G_TransactionResults getEvents(
			@QueryParam("identifiers") @DefaultValue(value = "") String identifiers,
			@QueryParam("offset") @DefaultValue(value = "0") int offset,
			@QueryParam("limit") @DefaultValue(value = "1000") int limit,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments,
			@QueryParam("intersection") @DefaultValue(value = "false") boolean intersection);

	@Log
	@Produces("application/json")
	@GET
	@Path("/between")
	G_TransactionResults getEvents(@QueryParam("from") @DefaultValue(value = "") String from,
			@QueryParam("to") @DefaultValue(value = "") String to,
			@QueryParam("offset") @DefaultValue(value = "0") int offset,
			@QueryParam("limit") @DefaultValue(value = "1000") int limit,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments,
			@QueryParam("intersection") @DefaultValue(value = "true") boolean intersection);
	// -----------------------------------------------------------------------

}