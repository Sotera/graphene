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

package graphene.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.InitializationPriority;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Append a loader icon to wait for zone update completion.
 * 
 * @author ccordenier
 */
public class AjaxLoader
{
    /**
     * The class of the element that shows the ajax loader image
     */
    @Parameter(value = "loading", defaultPrefix = BindingConstants.LITERAL)
    private String loaderClass;

    /**
     * The element to render. The default is derived from the component template.
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String element;

    /**
     * The zone to observe.
     */
    @Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    private String zone;

    /**
     * The id of the element that triggers the zone update.
     */
    @Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    private String trigger;

    @Inject
    private JavaScriptSupport javascriptSupport;

    @Inject
    private ComponentResources resources;

    private String loader;

    String defaultElement()
    {
        return resources.getElementName("div");
    }

    @BeginRender
    void initAjaxLoader(MarkupWriter writer)
    {
        loader = javascriptSupport.allocateClientId("loader");

        JSONObject data = new JSONObject();
        data.put("zone", zone);
        data.put("trigger", trigger);
        data.put("loader", loader);
        javascriptSupport.addInitializerCall(InitializationPriority.NORMAL, "initAjaxLoader", data);
    }

    @AfterRender
    void writeAjaxLoader(MarkupWriter writer)
    {
        writer.element(element, "id", loader, "class", this.loaderClass, "style", "display:none;");
        writer.end();
    }
}
