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

import graphene.model.idl.G_Constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * While this is a very simple DAO, if we intend to always pre-load this it may
 * be more readable to have an initialize() method as part of the interface.
 * Implementations that cache the values can use that hook to explicitly fill
 * the values.
 * 
 * OTOH, since the service is a singleton with no mutable state, it can just
 * lazy load without having to 'preload' anything.
 * 
 * @author PWG,djue
 * 
 */
public interface DataSourceListDAO {
	public static final String ALL_REPORTS = "All Reports";

	public List<G_Constraint> getAvailableConstraints();

	public List<String> getAvailableTypes();

	public abstract List<String> getAvailableTypes(final String schema);

	public String getDefaultSchema();

	public Map<String, ArrayList<String>> getFieldMappings();

	public abstract Map<String, ArrayList<String>> getRangeMappings();

}