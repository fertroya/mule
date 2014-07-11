/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.extensions.introspection.api.DataType;
import org.mule.extensions.introspection.api.ExtensionParameter;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Immutable implementation of {@link org.mule.extensions.introspection.api.ExtensionParameter}
 *
 * @since 1.0
 */
final class ImmutableExtensionParameter extends AbstractImmutableDescribed implements ExtensionParameter
{

    private static final Set<String> reservedNames = ImmutableSet.<String>builder().add("name").build();

    private final DataType type;
    private final boolean required;
    private final boolean dynamic;
    private final Object defaultValue;

    protected ImmutableExtensionParameter(String name,
                                          String description,
                                          DataType type,
                                          boolean required,
                                          boolean dynamic,
                                          Object defaultValue)
    {
        super(name, description);

        if (reservedNames.contains(name))
        {
            throw new IllegalArgumentException(
                    String.format("Extension parameter cannot have the name ['%s'] since it's a reserved one", name));
        }

        checkArgument(type != null, "Parameters must have a type");

        this.type = type;
        this.required = required;
        this.dynamic = dynamic;
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getType()
    {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRequired()
    {
        return required;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDynamic()
    {
        return dynamic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }

}