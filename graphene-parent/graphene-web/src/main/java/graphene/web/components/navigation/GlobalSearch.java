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

package graphene.web.components.navigation;

import graphene.dao.DataSourceListDAO;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.validator.ValidationUtils;
import graphene.web.pages.CombinedEntitySearchPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.SelectModelFactory;
import org.slf4j.Logger;

public class GlobalSearch {
	@Property
	private SelectModel availableModels;

	@Inject
	SelectModelFactory smf;

	@Persist
	@Property
	private String selectedType;

	@Inject
	private DataSourceListDAO dao;
	@Property
	private List<String> availableTypes;

	@Persist
	@Property
	private List<Integer> maxResultsList;

	@Inject
	private AlertManager alertManager;

	@Persist(PersistenceConstants.CLIENT)
	@Property
	private String searchValue;

	@Persist
	@Property
	private Integer selectedMaxResults;
	
	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	private Integer defaultMaxResults;
	
	@Inject
	private Logger logger;

	@InjectPage
	private CombinedEntitySearchPage searchPage;
	
	@OnEvent(value = EventConstants.SUCCESS, component = "globalSearchForm")
	Object onSuccessFromForm() {
		logger.debug("Searching with " + searchValue + " type: " + selectedType);
		Object retval = null;
		if (!ValidationUtils.isValid(selectedMaxResults)) {
			selectedMaxResults = defaultMaxResults;
		}
		if (ValidationUtils.isValid(searchValue)) {
			G_Constraint searchtype = G_Constraint.CONTAINS;

			if (searchValue.startsWith("\"") && searchValue.endsWith("\"")) {
				searchtype = G_Constraint.EQUALS;
			}
			final Link link = searchPage.set(dao.getDefaultSchema(), selectedType, searchtype.name(), searchValue,
					selectedMaxResults);
			retval = link;
		} else {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Please enter a valid search value.");
		}
		if (!ValidationUtils.isValid(retval)) {
			// alertManager.alert(Duration.TRANSIENT, Severity.WARN,
			// "There is no search broker configured for this instance of Graphene");
		}
		return retval;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "globalSearchForm")
	void onValidateFromForm() {
	    logger.debug("Validating");
		// The searchValue must be valid -- no script tags, etc.
		// The search type must be a valid type from the list
		// The search number must be a valid number from the list.
	}

	@SetupRender
	private void setupRender() {
		maxResultsList = new ArrayList<Integer>(3);
		maxResultsList.add(new Integer(200));
		maxResultsList.add(new Integer(500));
		maxResultsList.add(new Integer(1000));
		if (!ValidationUtils.isValid(selectedMaxResults)) {
			selectedMaxResults = defaultMaxResults;
		}
		if (!ValidationUtils.isValid(availableTypes)) {
			availableTypes = dao.getAvailableTypes();
			if (!ValidationUtils.isValid(availableTypes)) {
				logger.error("Could not get a list of types from the server.");
			} else {
				Collections.sort(availableTypes);
			}
		}
	}
}
