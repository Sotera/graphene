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

package graphene.augment.snlp.web.pages;

import graphene.augment.snlp.model.NERResult;
import graphene.augment.snlp.services.NERService;
import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.slf4j.Logger;

@PluginPage(visualType = G_VisualType.PLUGIN, menuName = "NER", icon = "fa fa-lg fa-fw fa-comments")
@Import(library = { "context:core/js/plugin/dropzone/dropzone.js", "context:/core/js/startdropzone.js" })
public class NER {
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Property
	private NERResult currentResult;
	@Property
	private UploadedFile file;
	@Property
	@Persist
	private boolean highlightZoneUpdates;
	@Property
	private int index;
	@InjectComponent
	private Zone listZone;
	@Component
	private Form mydropzone;

	@Inject
	private Logger logger;

	@Inject
	private Request request;

	@Persist
	private List<NERResult> nerresults;

	@Inject
	private NERService nerService;

	public List<NERResult> getResults() {
		return nerresults;
	}

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	public Object onSuccess() {

		final BufferedReader br = new BufferedReader(new InputStreamReader(file.getStream()));
		String line;
		nerresults = new ArrayList<NERResult>();
		try {
			while ((line = br.readLine()) != null) {
				logger.debug(line);
				nerresults.addAll(nerService.getResults(line));
			}
			logger.debug("Done extracting entities");
		} catch (final IOException e) {
			logger.error("onSuccess" + e.getMessage());
		}

		return request.isXHR() ? listZone.getBody() : null;
	}

	void setupRender() {

	}
}
