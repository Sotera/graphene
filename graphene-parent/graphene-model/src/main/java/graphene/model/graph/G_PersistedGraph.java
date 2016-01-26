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

package graphene.model.graph;

public class G_PersistedGraph {
	private java.lang.String id;

	String graphSeed;

	String username;

	/** workspace's modified datetime */
	private java.lang.Long modified;
	/** workspace's created datetime */
	private java.lang.Long created;
	String graphJSONdata;

	public java.lang.Long getCreated() {
		return created;
	}

	public String getGraphJSONdata() {
		return graphJSONdata;
	}

	public String getGraphSeed() {
		return graphSeed;
	}

	public java.lang.String getId() {
		return id;
	}

	public java.lang.Long getModified() {
		return modified;
	}

	public String getUserName() {
		return username;
	}

	public void setCreated(final java.lang.Long created) {
		this.created = created;
	}

	public void setGraphJSONdata(final String graphJSONdata) {
		this.graphJSONdata = graphJSONdata;
	}

	public void setGraphSeed(final String graphSeed) {
		this.graphSeed = graphSeed;
	}

	public void setId(final java.lang.String id) {
		this.id = id;
	}

	public void setModified(final java.lang.Long modified) {
		this.modified = modified;
	}

	public void setUserName(final String username) {
		this.username = username;
	}
}
