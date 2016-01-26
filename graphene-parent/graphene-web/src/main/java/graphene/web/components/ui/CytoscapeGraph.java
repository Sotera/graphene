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

package graphene.web.components.ui;

import graphene.util.validator.ValidationUtils;
import graphene.web.services.javascript.CytoscapeStack;
import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SupportsInformalParameters
@Import(stack = { CytoscapeStack.STACK_ID })
public class CytoscapeGraph implements ClientElement {

	private String assignedClientId;

	@Parameter(value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
	private String clientId;

	/**
	 * the list of data item arrays.
	 */
	@Parameter(name = "csgraph", required = false, defaultPrefix = BindingConstants.PROP)
	private V_CSGraph csGraphData;
	@Parameter(name = "options", required = false, defaultPrefix = BindingConstants.PROP)
	private JSONObject options;
	@Parameter(name = "graphTitle", required = false, defaultPrefix = BindingConstants.LITERAL)
	private String graphTitle;

	/**
	 * PageRenderSupport to get unique client side id.
	 */
	@Environmental
	private JavaScriptSupport javascriptSupport;

	/**
	 * For blocks, messages, create actionlink, trigger event.
	 */
	@Inject
	private ComponentResources resources;
	@Inject
	private Logger logger;

	/**
	 * Tapestry render phase method. End a tag here.
	 */
	@AfterRender
	void afterRender() {
		JSONObject config = new JSONObject();

		//
		// Let subclasses do more.
		//
		configure(config);

		// Set Graph Title if it is provided
		JSONObject optionObjRef = null;
		if (ValidationUtils.isValid(graphTitle)) {
			try {
				optionObjRef = (JSONObject) config.get("options");
			} catch (RuntimeException re) {
				// Some graphs might not have option object at all. We need to
				// set option object and title both in that case.
				if (re.getMessage().endsWith("not found.")) {
					optionObjRef = new JSONObject();
				}
			}
			if (optionObjRef != null) {
				optionObjRef.put("title", graphTitle.trim());
				config.put("options", optionObjRef);
			}
		}

		//
		// if the user doesn't give us some chart values we add an empty value
		// array.
		//
		if (options != null && csGraphData != null) {
			ObjectMapper mapper = new ObjectMapper();

			try {
				JSONObject jsonCSGraph = new JSONObject(
						mapper.writeValueAsString(csGraphData));
				if (ValidationUtils.isValid(jsonCSGraph)) {
					options.put("elements", jsonCSGraph);
				} else {
					logger.error("Could not convert graph to json");
				}
			} catch (JsonProcessingException e) {
				logger.error("Could not convert graph to json: "
						+ e.getMessage());
			}
			options.put("container", "document.getElementById('"
					+ getClientId() + "')");

//			JSONObject layoutObject = new JSONObject();
//			layoutObject.put("name", "'concentric'");
//			layoutObject.put("concentric",
//					"function() { return this.data('score'); }");
//			layoutObject.put("levelWidth", "function(nodes) { return 0.5; }");
//			layoutObject.put("padding", 10);
//			options.put("layout", layoutObject);
			options.put("ready",
					"function() {window.cy = this; cy.elements().unselectify();  } ");

			options.put("id", getClientId());
			// spec.put("options", options);

			javascriptSupport.addInitializerCall("cytoscapeGraph", options);
		}
	}

	/**
	 * Tapestry render phase method. Start a tag here, end it in afterRender
	 */
	@BeginRender
	void beginRender(MarkupWriter writer) {
		writer.element("div", "id", getClientId());
		resources.renderInformalParameters(writer);
		writer.end();
	}

	/**
	 * Invoked to allow subclasses to further configure the parameters passed to
	 * this component's javascript options. Subclasses may override this method
	 * to configure additional features of the Flotr.
	 * <p/>
	 * This implementation does nothing.
	 * 
	 * @param config
	 *            parameters object
	 */
	protected void configure(JSONObject config) {
	}

	/**
	 * Returns a unique id for the element. This value will be unique for any
	 * given rendering of a page. This value is intended for use as the id
	 * attribute of the client-side element, and will be used with any
	 * DHTML/Ajax related JavaScript.
	 */
	@Override
	public String getClientId() {
		return assignedClientId;
	}

	/**
	 * Tapestry render phase method. Initialize temporary instance variables
	 * here.
	 */
	@SetupRender
	void setupRender() {
		assignedClientId = javascriptSupport.allocateClientId(clientId);
	}
}
