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

package org.apache.tapestry5.bindings.internal.services;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.bindings.internal.ExpressionToken;
import org.apache.tapestry5.bindings.internal.JSONArrayBinding;
import org.apache.tapestry5.bindings.internal.JSONBinding;
import org.apache.tapestry5.internal.services.StringInterner;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.BindingFactory;

public class JSONBindingFactory implements BindingFactory {

    private final BindingFactory propBindingFactory;

    private final StringInterner interner;

    public JSONBindingFactory(@InjectService("PropBindingFactory")
                              final BindingFactory propBindingFactory,
                              final StringInterner interner) {

        this.propBindingFactory = propBindingFactory;
        this.interner = interner;
    }

    public Binding newBinding(final String description,
                              final ComponentResources container,
                              final ComponentResources component,
                              final String expression,
                              final Location location) {

        final String[] expressions = expression.split("\\.", 2);

        final ExpressionToken expressionToken = new ExpressionToken(expressions[0]);
        final Binding propBinding = firstPartBinding(description, container, component, location, expressionToken);

        if (expressions.length == 1) {

            return propBinding;
        }

        final String prop = expressions[1];
        final String toString = interner.format("JSONBinding[%s %s(%s)]", description, container
                        .getCompleteId(), expression);

        return new JSONBinding(location, prop, propBinding, expressionToken.isNextOptional(), toString);
    }

    private Binding firstPartBinding(final String description,
                                     final ComponentResources container,
                                     final ComponentResources component,
                                     final Location location,
                                     final ExpressionToken expressionToken) {

        final String property = expressionToken.getProperty();
        final Binding propBinding = propBindingFactory.newBinding(description, container, component, property, location);

        if (expressionToken.isArray()) {

            final String toString = interner.format("JSONBinding[%s %s(%s)]", description, container.getCompleteId(), property);

            return new JSONArrayBinding(location, expressionToken, propBinding, toString);
        }

        return propBinding;
    }
}
