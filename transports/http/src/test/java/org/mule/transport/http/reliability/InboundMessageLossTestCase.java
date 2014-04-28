/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.reliability;

import static org.junit.Assert.assertEquals;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.exception.AbstractMessagingExceptionStrategy;
import org.mule.exception.DefaultSystemExceptionStrategy;
import org.mule.message.DefaultExceptionPayload;
import org.mule.routing.filters.WildcardFilter;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.NullPayload;
import org.mule.transport.http.HttpConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;

/**
 * Verify that no inbound messages are lost when exceptions occur.
 * The message must either make it all the way to the SEDA queue (in the case of
 * an asynchronous inbound endpoint), or be restored/rolled back at the source.
 * 
 * In the case of the HTTP transport, there is no way to restore the source message
 * so an exception is simply returned to the client.
 */
public class InboundMessageLossTestCase extends FunctionalTestCase
{
    protected HttpClient httpClient = HttpClients.createMinimal();
    
    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");
    
    @Override
    protected String getConfigFile()
    {
        return "reliability/inbound-message-loss.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        
        // Set SystemExceptionStrategy to redeliver messages (this can only be configured programatically for now)
        ((DefaultSystemExceptionStrategy) muleContext.getExceptionListener()).setRollbackTxFilter(new WildcardFilter("*"));
    }

    @Test
    public void testNoException() throws Exception
    {
        HttpGet request = createRequest(getBaseUri() + "/noException");
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpConstants.SC_OK, response.getStatusLine().getStatusCode());
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //assertEquals("Here you go", request.getResponseBodyAsString());
    }
    
    @Test
    public void testTransformerException() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HttpMethodBase request = createRequest(getBaseUri() + "/transformerException");
        //int status = httpClient.executeMethod(request);
        //assertEquals(HttpConstants.SC_INTERNAL_SERVER_ERROR, status);
        //assertTrue(request.getResponseBodyAsString().contains("Failure"));
    }

    @Test
    public void testHandledTransformerException() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HttpMethodBase request = createRequest(getBaseUri() + "/handledTransformerException");
        //int status = httpClient.executeMethod(request);
        //assertEquals(HttpConstants.SC_OK, status);
        //assertTrue(request.getResponseBodyAsString().contains("Success"));
    }

    @Test
    public void testNotHandledTransformerException() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HttpMethodBase request = createRequest(getBaseUri() + "/notHandledTransformerException");
        //int status = httpClient.executeMethod(request);
        //assertEquals(HttpConstants.SC_INTERNAL_SERVER_ERROR, status);
        //assertTrue(request.getResponseBodyAsString().contains("Bad news"));
    }

    @Test
    public void testRouterException() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HttpMethodBase request = createRequest(getBaseUri() + "/routerException");
        //int status = httpClient.executeMethod(request);
        //assertEquals(HttpConstants.SC_INTERNAL_SERVER_ERROR, status);
        //assertTrue(request.getResponseBodyAsString().contains("Failure"));
    }
    
    @Test
    public void testComponentException() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HttpMethodBase request = createRequest(getBaseUri() + "/componentException");
        //int status = httpClient.executeMethod(request);
        //// Component exception occurs after the SEDA queue for an asynchronous request, but since
        //// this request is synchronous, the failure propagates back to the client.
        //assertEquals(HttpConstants.SC_INTERNAL_SERVER_ERROR, status);
        //assertTrue(request.getResponseBodyAsString().contains("exception"));
    }

    protected HttpGet createRequest(String uri)
    {
        return new HttpGet(uri);
    }
    
    protected String getBaseUri()
    {
        return "http://localhost:" + dynamicPort.getNumber();
    }

    /**
     * Custom Exception Handler that handles an exception
     */
    public static class Handler extends AbstractMessagingExceptionStrategy
    {
        public Handler(MuleContext muleContext)
        {
            super(muleContext);
        }

        @Override
        public MuleEvent handleException(Exception ex, MuleEvent event)
        {
            doHandleException(ex, event);
            ((MessagingException)ex).setHandled(true);
            return new DefaultMuleEvent(new DefaultMuleMessage("Success!", muleContext), event);
        }
    }

    /**
     * Custom Exception Handler that creates a different exception
     */
    public static class BadHandler extends AbstractMessagingExceptionStrategy
    {
        public BadHandler(MuleContext muleContext)
        {
            super(muleContext);
        }

        @Override
        public MuleEvent handleException(Exception ex, MuleEvent event)
        {
            doHandleException(ex, event);
            DefaultMuleMessage message = new DefaultMuleMessage(NullPayload.getInstance(), muleContext);
            message.setExceptionPayload(
                new DefaultExceptionPayload(new MessagingException(event, new RuntimeException("Bad news!"))));
            return new DefaultMuleEvent(message, event);
        }
    }
}
