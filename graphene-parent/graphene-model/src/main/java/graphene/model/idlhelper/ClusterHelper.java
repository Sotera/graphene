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

/**
 * Copyright (c) 2013-2014 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package graphene.model.idlhelper;

import graphene.model.idl.G_Cluster;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_Uncertainty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClusterHelper extends G_Cluster {
	public ClusterHelper(String id, List<G_EntityTag> tagList, G_Provenance provenance, G_Uncertainty uncertainty, List<G_Property> properties, List<java.lang.String> members, List<java.lang.String> subclusters, String parent, String root, Integer level) {
		super(id, new ArrayList<G_EntityTag>(tagList), provenance, uncertainty, new ArrayList<G_Property>(properties), new ArrayList<String>(members), new ArrayList<String>(subclusters), parent, root, level, 1);
	}

	public ClusterHelper(String id, String label, List<G_EntityTag> tagList, List<G_Property> properties, List<java.lang.String> members, List<java.lang.String> subclusters, String parent, String root, Integer level) {
		this(id, tagList, null, null, merge(properties, Collections.singletonList((G_Property)new PropertyHelper(G_PropertyTag.LABEL, label))),
				members, subclusters, parent, root, level);
	}

	public ClusterHelper(String id, String label, G_EntityTag tag, List<G_Property> properties, List<java.lang.String> members, List<java.lang.String> subclusters, String parent, String root, Integer level) {
		this(id, label, Collections.singletonList(tag), properties, members, subclusters, parent, root, level);
	}

	public ClusterHelper(String id, String label, G_EntityTag tag, List<G_Property> properties, String parent, String root, Integer level) {
		this(id, label, Collections.singletonList(tag), properties, new ArrayList<String>(), new ArrayList<String>(), parent, root, level);
	}
	
	public PropertyHelper getFirstProperty(String key) {
		for (G_Property property : getProperties()) {
			if (property.getKey().equals(key)) return PropertyHelper.from(property);
		}
		return null;
	}

	private static List<G_Property> merge(List<G_Property> list1, List<G_Property> list2) {
		List<G_Property> merged = new ArrayList<G_Property>(list1);
		merged.addAll(list2);
		return merged;
	}
	
	public String getId() {
		return (String)getUid();
	}

	public String getLabel() {
		PropertyHelper label = getFirstProperty(G_PropertyTag.LABEL.name());
		return (String) (label != null ? label.getValue() : null); 
	}
	
	public boolean isEmpty(G_Cluster cluster) {
		return cluster.getMembers().isEmpty() && cluster.getSubclusters().isEmpty();
	}
	
	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}
	
	public static String toJson(G_Cluster cluster) throws IOException {
		return SerializationHelper.toJson(cluster);
	}
	
	public static String toJson(List<G_Cluster> clusters) throws IOException {
		return SerializationHelper.toJson(clusters, G_Cluster.getClassSchema());
	}
	
	public static String toJson(Map<String, List<G_Cluster>> clusters) throws IOException {
		return SerializationHelper.toJson(clusters, G_Cluster.getClassSchema());
	}
	
	public static G_Cluster fromJson(String json) throws IOException {
		return SerializationHelper.fromJson(json, G_Cluster.getClassSchema());
	}
	
	public static List<G_Cluster> listFromJson(String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_Cluster.getClassSchema());
	}
	
	public static Map<String,List<G_Cluster>> mapFromJson(String json) throws IOException {
		return SerializationHelper.mapFromJson(json, G_Cluster.getClassSchema());
	}
	
	public static PropertyHelper getFirstProperty(G_Cluster cluster, String key) {
		for (G_Property property : cluster.getProperties()) {
			if (property.getKey().equals(key)) return PropertyHelper.from(property);
		}
		return null;
	}
	
	public static PropertyHelper getFirstPropertyByTag(G_Cluster cluster, G_PropertyTag tag) {
		for (G_Property property : cluster.getProperties()) {
			if (property.getTags().contains(tag)) return PropertyHelper.from(property);
		}
		return null;
	}
	
	public static void incrementVersion(G_Cluster cluster) {
		Integer version = cluster.getVersion() + 1;
		cluster.setVersion(version);
	}
	
	public static void addMemberById(G_Cluster cluster, String entityId) {
		cluster.getMembers().add(entityId);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void addMembersById(G_Cluster cluster, List<String> entityIds) {
		cluster.getMembers().addAll(entityIds);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void addMember(G_Cluster cluster, G_Entity entity) {
		_addMember(cluster, entity);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	private static void _addMember(G_Cluster cluster, G_Entity entity) {
		cluster.getMembers().add(entity.getUid());
	}
	
	public static void addMembers(G_Cluster cluster, List<G_Entity> entities) {
		for (G_Entity entity : entities) {
			_addMember(cluster, entity);
		}
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void addSubClusterById(G_Cluster cluster, String subClusterId) {
		cluster.getSubclusters().add(subClusterId);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void addSubClustersById(G_Cluster cluster, List<String> subClusterIds) {
		cluster.getSubclusters().addAll(subClusterIds);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void addSubCluster(G_Cluster cluster, G_Cluster subCluster) {
		_addSubCluster(cluster, subCluster);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	private static void _addSubCluster(G_Cluster cluster, G_Cluster subCluster) {
		cluster.getSubclusters().add(subCluster.getUid());
	}
	
	public static void addSubClusters(G_Cluster cluster, List<G_Cluster> subClusters) {
		for (G_Cluster subCluster : subClusters) {
			_addSubCluster(cluster, subCluster);
		}
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void removeMemberById(G_Cluster cluster, String entityId) {
		cluster.getMembers().remove(entityId);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void removeMembersById(G_Cluster cluster, List<String> entityIds) {
		cluster.getMembers().removeAll(entityIds);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void removeMember(G_Cluster cluster, G_Entity entity) {
		_removeMember(cluster, entity);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	private static void _removeMember(G_Cluster cluster, G_Entity entity) {
		cluster.getMembers().remove(entity.getUid());
	}
	
	public static void removeMembers(G_Cluster cluster, List<G_Entity> entities) {
		for (G_Entity entity : entities) {
			_removeMember(cluster, entity);
		}
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void removeSubClusterById(G_Cluster cluster, String subClusterId) {
		cluster.getSubclusters().remove(subClusterId);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void removeSubClustersById(G_Cluster cluster, List<String> subClusterIds) {
		cluster.getSubclusters().addAll(subClusterIds);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	public static void removeSubCluster(G_Cluster cluster, G_Cluster subCluster) {
		_removeSubCluster(cluster, subCluster);
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
	private static void _removeSubCluster(G_Cluster cluster, G_Cluster subCluster) {
		cluster.getSubclusters().remove(subCluster.getUid());
	}
	
	public static void removeSubClusters(G_Cluster cluster, List<G_Cluster> subClusters) {
		for (G_Cluster subCluster : subClusters) {
			_removeSubCluster(cluster, subCluster);
		}
		// increment the cluster version number
		incrementVersion(cluster);
	}
	
}
