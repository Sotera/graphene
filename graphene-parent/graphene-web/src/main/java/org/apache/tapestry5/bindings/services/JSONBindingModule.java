// Copyright 2012 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.bindings.services;

import java.util.List;

import org.apache.tapestry5.bindings.JSONBindingConstants;
import org.apache.tapestry5.bindings.internal.services.JSONBindingFactory;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.services.ChainBuilder;
import org.apache.tapestry5.services.BindingFactory;

public class JSONBindingModule {

    private final ChainBuilder chainBuilder;

    public JSONBindingModule(final ChainBuilder chainBuilder) {

        this.chainBuilder = chainBuilder;
    }

    public BindingFactory buildJSONBindingFactory(List<BindingFactory> configuration, @Autobuild JSONBindingFactory service) {

        configuration.add(service);

        return chainBuilder.build(BindingFactory.class, configuration);
    }

    public void contributeBindingSource(MappedConfiguration<String, BindingFactory> configuration) {

        configuration.addInstance(JSONBindingConstants.JSON, JSONBindingFactory.class);
    }
}