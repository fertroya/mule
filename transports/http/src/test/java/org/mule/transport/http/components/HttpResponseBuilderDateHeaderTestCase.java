/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.components;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import org.mule.api.MuleContext;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.junit4.rule.CustomTimeZone;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.http.HttpResponse;

import java.util.Date;

import org.apache.commons.httpclient.Header;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class HttpResponseBuilderDateHeaderTestCase extends AbstractMuleTestCase
{
    @Rule
    public CustomTimeZone timeZone = new CustomTimeZone("EST");

    private MuleContext muleContext;

    @Before
    public void setUp()
    {
        muleContext = mock(MuleContext.class);
    }

    @Test
    public void testDateHeaderFormat() throws Exception
    {
        HttpResponseBuilder httpResponseBuilder = createHttpResponseBuilder();
        HttpResponse response = new HttpResponse();

        DateTime dateTime = new DateTime(2005, 9, 5, 16, 30, 0, 0, DateTimeZone.forID("EST"));

        Date date = new Date(dateTime.getMillis());

        httpResponseBuilder.setDateHeader(response, date);

        boolean headerFound = false;
        for (Header header : response.getHeaders())
        {
            if (HttpConstants.HEADER_DATE.equals(header.getName()))
            {
                headerFound = true;
                assertThat(header.getValue(), equalTo(getExpectedHeaderValue()));
            }
        }
        assertTrue("Date header missing.", headerFound);
    }

    protected String getExpectedHeaderValue()
    {
        return "Mon, 05 Sep 2005 21:30:00 +0000";
    }

    private HttpResponseBuilder createHttpResponseBuilder() throws InitialisationException
    {
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();
        httpResponseBuilder.setMuleContext(muleContext);
        httpResponseBuilder.initialise();
        return httpResponseBuilder;
    }
}
