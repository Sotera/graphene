package org.apache.tapestry5.bindings.internal;

public class ExpressionToken {

    private final String property;

    private final boolean nextOptional;

    private final boolean array;

    private final int arrayIndex;

    public ExpressionToken(final String expression) {

        int arrayIndex = -1;
        String property = expression;
        this.nextOptional = expression.endsWith("?");

        if (nextOptional) {

            property = property.split("\\?")[0];
        }

        boolean array = property.endsWith("]");
        if (array) {

            final String[] arrayParts = property.split("\\[");

            property = arrayParts[0];
            arrayIndex = Integer.parseInt(arrayParts[1].split("\\]")[0]);
        }

        this.property = property;
        this.array = array;
        this.arrayIndex = arrayIndex;
    }

    public String getProperty() {

        return property;
    }

    public boolean isNextOptional() {

        return nextOptional;
    }

    public boolean isArray() {

        return array;
    }

    public int getArrayIndex() {

        return arrayIndex;
    }

}