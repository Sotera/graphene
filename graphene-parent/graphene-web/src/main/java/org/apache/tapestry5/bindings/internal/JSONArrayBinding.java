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

package org.apache.tapestry5.bindings.internal;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.json.JSONArray;

public class JSONArrayBinding extends JSONBinding {

    private final ExpressionToken expressionToken;

    public JSONArrayBinding(final Location location,
                            final ExpressionToken expressionToken,
                            final Binding binding,
                            final String toString) {

        super(location, expressionToken.getProperty(), binding, false, toString);

        this.expressionToken = expressionToken;
    }

    @Override
    public Object get() {

        return ((JSONArray) binding.get()).get(expressionToken.getArrayIndex());
    }
}
