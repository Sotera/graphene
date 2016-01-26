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

package graphene.dao;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementations of this class are responsible for creating a
 * {@link G_SearchResult} from a {@link JsonNode}.
 * 
 * The implementation may delegate to one or more {@link G_Parser} if there are
 * multiple types of documents that must be handled differently.
 * 
 * @author djue
 * 
 */

public interface DocumentBuilder {

	G_SearchResult buildSearchResultFromDocument(final int index, final JsonNode hit, final G_EntityQuery sq);

	G_Parser getParserForObject(String obj);

}
