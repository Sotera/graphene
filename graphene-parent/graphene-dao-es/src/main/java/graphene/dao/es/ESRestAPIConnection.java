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

package graphene.dao.es;

import io.searchbox.client.JestClient;

public interface ESRestAPIConnection {

	public abstract void createIndex(String indexName, int shards, int replicas);

	public abstract void createIndex(String indexName, String settings);

	public abstract void deleteIndex(String indexName) throws Exception;

	public abstract JestClient getClient();

	public abstract String getIndexName();

	public abstract void setIndexName(String indexName);

}