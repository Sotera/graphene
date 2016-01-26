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

package graphene.augment.mitie.web.pages;

import graphene.augment.mitie.dao.MitieDAO;
import graphene.augment.mitie.model.MitieEntity;
import graphene.augment.mitie.model.MitieResponse;
import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_VisualType;
import graphene.util.validator.ValidationUtils;
import graphene.web.annotations.PluginPage;
import graphene.web.pages.CombinedEntitySearchPage;
import graphene.web.pages.SimpleBasePage;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

@PluginPage(visualType = G_VisualType.SEARCH, menuName = "Entity Extraction", icon = "fa fa-lg fa-fw fa-cogs")
public class EntityExtraction extends SimpleBasePage {

	@Inject
	private Logger logger;
	@Inject
	private MitieDAO dao;
	@Property
	@Persist
	private String textReturnValue;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String textAreaValue;
	
    @Inject
    @Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
    private Integer defaultMaxResults;
    
    @InjectPage
    private CombinedEntitySearchPage searchPage;
    
	@Property
	@Persist
	private MitieResponse r;
	@Property
	@Persist
	private MitieEntity currentEntity;
	@Inject
	private AlertManager alertManager;

	@Property
	@Component(id = "AugmentTextForm")
	private Form augmentTextForm;

	@Inject
	private Request request;

	@InjectComponent
	private Zone extractionZone;

	@OnEvent(value = "cancel")
	void cancel() {
		textAreaValue = null;
	}

	public String getNumberOfEntitiesExtracted() {
		int results = 0;
		if ((r != null) && (r.getEntities() != null)) {
			results = r.getEntities().size();
		}
		if (results == 0) {
			return "No entities extracted";
		} else if (results == 1) {
			return "One entity extracted";
		} else {
			return "" + results + " entities extracted";
		}
	}
	
    public Link getNamePivotLink(final String term) {
        logger.debug("getNamePivotLink");
        // XXX: pick the right search type based on the link value
        final Link l = searchPage.set(null, null, G_Constraint.EQUALS.name(), term, defaultMaxResults);
        return l;
    }

	public String getStyleIcon() {
		String style;
		switch (currentEntity.getTag().toLowerCase()) {

		case "organization":
			style = "fa fa-lg fa-fw fa-building";
			break;
		case "person":
			style = "fa fa-lg fa-fw fa-user";
			break;
		case "location":
			style = "fa fa-lg fa-fw fa-map-marker";
			break;
		case "misc":
			style = "fa fa-lg fa-fw fa-question-circle";
			break;
		default:
			style = "fa fa-lg fa-fw fa-question";
		}
		return style;
	}

	Object onFailureFromAugmentTextForm() {
		r = null;
		currentEntity = null;
		return request.isXHR() ? extractionZone.getBody() : null;
	}

	Object onSuccessFromAugmentTextForm() {
		logger.debug("Set value to " + textAreaValue);
		r = null;
		currentEntity = null;
		if (ValidationUtils.isValid(textAreaValue)) {
			try {
				textAreaValue = textAreaValue.trim();
				r = dao.augment(textAreaValue);

			} catch (final DataAccessException e) {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, e.getMessage());
			}
		} else {
			alertManager.alert(Duration.TRANSIENT, Severity.INFO, "Please enter text to extract entities from.");
		}
		return request.isXHR() ? extractionZone.getBody() : null;
	}

	void onValidateFromAugmentTextForm() {
		r = null;
		currentEntity = null;
		if (!ValidationUtils.isValid(textAreaValue)) {
			augmentTextForm.recordError("Please enter text to extract entities from.");
		}
	}

	public void setupRender() {

	}
}
