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
 * 
 */
package graphene.services;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_EntityQueryEvent;
import graphene.model.idl.G_ExportEvent;
import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_UserLoginEvent;

import java.util.List;

import mil.darpa.vande.interactions.TemporalGraphQuery;

/**
 * An No Operation (NoOp) implementation of the logging interface. It doesn't do
 * anything, but gives you a class to bind to.
 * 
 * @author djue
 * 
 */
public class LoggingDAONoOpImpl implements LoggingDAO {

	@Override
	public List<Object> getAllEvents(final String userId, final String partialTerm, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TemporalGraphQuery> getGraphQueries(final String userId, final String partialTerm, final int offset,
			final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_GraphViewEvent> getGraphViewEvents(final String userId, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_EntityQuery> getQueries(final String userId, final String partialTerm, final int offset,
			final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_ReportViewEvent> getReportViewEvents(final String userId, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserLoginEvent> getUserLoginEvents(final String userId, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordExportEvent(final G_ExportEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordGraphViewEvent(final G_GraphViewEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordQueryEvent(final G_EntityQueryEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordReportViewEvent(final G_ReportViewEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordUserLoginEvent(final G_UserLoginEvent e) {
		// TODO Auto-generated method stub

	}
}
