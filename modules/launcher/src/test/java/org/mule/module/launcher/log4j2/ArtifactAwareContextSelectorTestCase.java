/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher.log4j2;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mule.module.launcher.application.CompositeApplicationClassLoader;
import org.mule.module.launcher.artifact.ShutdownListener;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class ArtifactAwareContextSelectorTestCase extends AbstractMuleTestCase
{

    private ArtifactAwareContextSelector selector;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CompositeApplicationClassLoader classLoader;

    @Before
    public void before()
    {
        selector = new ArtifactAwareContextSelector();
        when(classLoader.getArtifactName()).thenReturn(getClass().getName());
    }

    @Test
    public void classLoaderToContext()
    {
        MuleLoggerContext context = (MuleLoggerContext) selector.getContext("", classLoader, true);
        assertSame(context, selector.getContext("", classLoader, true));

        classLoader = mock(CompositeApplicationClassLoader.class, RETURNS_DEEP_STUBS);
        when(classLoader.getArtifactName()).thenReturn(getClass().getName());
        assertNotSame(context, selector.getContext("", classLoader, true));
    }

    @Test
    public void shutdownListener()
    {
        MuleLoggerContext context = (MuleLoggerContext) selector.getContext("", classLoader, true);

        ArgumentCaptor<ShutdownListener> captor = ArgumentCaptor.forClass(ShutdownListener.class);
        verify(classLoader).addShutdownListener(captor.capture());
        ShutdownListener listener = captor.getValue();
        assertNotNull(listener);

        assertSame(context, selector.getContext("", classLoader, true));
        listener.execute();

        assertNotSame(context, selector.getContext("", classLoader, true));
    }


}
