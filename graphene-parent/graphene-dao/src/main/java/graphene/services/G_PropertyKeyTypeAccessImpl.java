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

package graphene.services;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_PropertyKey;
import graphene.model.idl.G_PropertyKeyTypeAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avro.AvroRemoteException;

public class G_PropertyKeyTypeAccessImpl implements G_PropertyKeyTypeAccess {
	private Map<String, G_PropertyKey> map = new TreeMap<String, G_PropertyKey>();

	public G_PropertyKeyTypeAccessImpl(Map<String, G_PropertyKey> map) {
		this.map.putAll(map);
	}

	@Override
	public List<G_PropertyKey> getPropertyKeys() throws AvroRemoteException {
		return new ArrayList<G_PropertyKey>(map.values());
	}

	@Override
	public G_PropertyKey getPropertyKey(String id) throws AvroRemoteException {
		return map.get(id);
	}

	@Override
	public G_PropertyKey getCommonPropertyKey(G_CanonicalPropertyType type)
			throws AvroRemoteException {
		return map.get(type.name());
	}

}
