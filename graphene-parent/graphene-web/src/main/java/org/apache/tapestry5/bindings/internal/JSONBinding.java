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
import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

public class JSONBinding extends AbstractBinding {

    protected final String prop;

    protected final Binding binding;

    protected final boolean optional;

    protected final String toString;

    public JSONBinding(final Location location,
                       final String prop,
                       final Binding binding,
                       final boolean optional,
                       final String toString) {

        super(location);

        this.prop = prop;
        this.binding = binding;
        this.optional = optional;
        this.toString = toString;
    }

    public Object get() {

        final JSONObject jsonObject = (JSONObject) binding.get();

        final String[] expressions = prop.split("\\.");
        ExpressionToken expressionToken = new ExpressionToken(expressions[0]);
        Object result = resolve(expressionToken, jsonObject, this.optional);

        if (expressions.length == 1) {

            return result;
        }

        boolean optional;
        for (int i = 1; i < expressions.length; i++) {

            if (result == null) {

                break;
            }

            optional = expressionToken.isNextOptional();
            expressionToken = new ExpressionToken(expressions[i]);

            result = resolve(expressionToken, (JSONObject) result, optional);
        }

        return result;
    }

    private Object resolve(final ExpressionToken expressionToken,
                           final JSONObject jsonObject,
                           final boolean optional) {

        final String property = expressionToken.getProperty();

        if (expressionToken.isArray()) {

            final JSONArray jsonArray = jsonObject.getJSONArray(property);
            if (optional && jsonArray.length() >= expressionToken.getArrayIndex()) {

                return null;
            }

            return jsonArray.get(expressionToken.getArrayIndex());
        }

        if (optional && !jsonObject.has(property)) {

            return null;
        }

        return jsonObject.get(property);
    }

    @Override
    public boolean isInvariant() {

        return false;
    }

    @Override
    public String toString() {

        return toString;
    }
}
