/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.functional;

import static org.junit.Assert.assertEquals;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.http.HttpConstants;
import org.mule.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class HttpKeepAliveFunctionalTestCase extends AbstractServiceAndFlowTestCase
{

    private static final String IN_CONNECTOR_NO_KEEP_ALIVE_EP_NO_KEEP_ALIVE = "inConnectorNoKeepAliveEpNoKeepAlive";
    private static final String IN_CONNECTOR_KEEP_ALIVE_EP_KEEP_ALIVE = "inConnectorKeepAliveEpKeepAlive";
    private static final String IN_CONNECTOR_NO_KEEP_ALIVE_EP_KEEP_ALIVE = "inConnectorNoKeepAliveEpKeepAlive";
    private static final String IN_CONNECTOR_KEEP_ALIVE_EP_NO_KEEP_ALIVE = "inConnectorKeepAliveEpNoKeepAlive";
    private static final String IN_CONNECTOR_NO_KEEP_ALIVE_EP_EMPTY = "inConnectorNoKeepAliveEpEmpty";
    private static final String IN_CONNECTOR_KEEP_ALIVE_EP_EMPTY = "inConnectorKeepAliveEpEmpty";

    private static final String CLOSE = "close";
    private static final String KEEP_ALIVE = "Keep-Alive";
    private static final String EMPTY = "";

    @Rule
    public DynamicPort dynamicPort1 = new DynamicPort("port1");

    @Rule
    public DynamicPort dynamicPort2 = new DynamicPort("port2");

    @Rule
    public DynamicPort dynamicPort3 = new DynamicPort("port3");

    @Rule
    public DynamicPort dynamicPort4 = new DynamicPort("port4");

    @Rule
    public DynamicPort dynamicPort5 = new DynamicPort("port5");

    @Rule
    public DynamicPort dynamicPort6 = new DynamicPort("port6");

    public HttpKeepAliveFunctionalTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][] {
                {ConfigVariant.SERVICE, "http-keep-alive-config-service.xml"},
                {ConfigVariant.FLOW, "http-keep-alive-config-flow.xml"}
        });
    }

    @Test
    public void testHttp10ConnectorKeepAliveEpEmpty() throws Exception
    {
        doTestKeepAliveInHttp10(getEndpointAddress(IN_CONNECTOR_KEEP_ALIVE_EP_EMPTY));
    }

    @Test
    public void testHttp10ConnectorNoKeepAliveEpEmpty() throws Exception
    {
        doTestNoKeepAliveInHttp10(getEndpointAddress(IN_CONNECTOR_NO_KEEP_ALIVE_EP_EMPTY));
    }

    @Test
    public void testHttp10ConnectorKeepAliveEpNoKeepAlive() throws Exception
    {
        doTestNoKeepAliveInHttp10(getEndpointAddress(IN_CONNECTOR_KEEP_ALIVE_EP_NO_KEEP_ALIVE));
    }

    @Test
    public void testHttp10ConnectorNoKeepAliveEpKeepAlive() throws Exception
    {
        doTestKeepAliveInHttp10(getEndpointAddress(IN_CONNECTOR_NO_KEEP_ALIVE_EP_KEEP_ALIVE));
    }

    @Test
    public void testHttp10ConnectorKeepAliveEpKeepAlive() throws Exception
    {
        doTestKeepAliveInHttp10(getEndpointAddress(IN_CONNECTOR_KEEP_ALIVE_EP_KEEP_ALIVE));
    }

    @Test
    public void testHttp10ConnectorNoKeepAliveEpNoKeepAlive() throws Exception
    {
        doTestNoKeepAliveInHttp10(getEndpointAddress(IN_CONNECTOR_NO_KEEP_ALIVE_EP_NO_KEEP_ALIVE));
    }

    @Test
    public void testHttp11ConnectorKeepAliveEpEmpty() throws Exception
    {
        doTestKeepAliveInHttp11(getEndpointAddress(IN_CONNECTOR_KEEP_ALIVE_EP_EMPTY));
    }

    @Test
    public void testHttp11ConnectorNoKeepAliveEpEmpty() throws Exception
    {
        doTestNoKeepAliveInHttp11(getEndpointAddress(IN_CONNECTOR_NO_KEEP_ALIVE_EP_EMPTY));
    }

    @Test
    public void testHttp11ConnectorKeepAliveEpNoKeepAlive() throws Exception
    {
        doTestNoKeepAliveInHttp11(getEndpointAddress(IN_CONNECTOR_KEEP_ALIVE_EP_NO_KEEP_ALIVE));
    }

    @Test
    public void testHttp11ConnectorNoKeepAliveEpKeepAlive() throws Exception
    {
        doTestKeepAliveInHttp11(getEndpointAddress(IN_CONNECTOR_NO_KEEP_ALIVE_EP_KEEP_ALIVE));
    }

    @Test
    public void testHttp11ConnectorKeepAliveEpKeepAlive() throws Exception
    {
        doTestKeepAliveInHttp11(getEndpointAddress(IN_CONNECTOR_KEEP_ALIVE_EP_KEEP_ALIVE));
    }

    @Test
    public void testHttp11ConnectorNoKeepAliveEpNoKeepAlive() throws Exception
    {
        doTestNoKeepAliveInHttp11(getEndpointAddress(IN_CONNECTOR_NO_KEEP_ALIVE_EP_NO_KEEP_ALIVE));
    }

    private void doTestKeepAliveInHttp10(String endpointAddress) throws Exception
    {
        HttpClient httpClient = setupHttpClient(HttpVersion.HTTP_1_0);

        doTestHttp(endpointAddress, EMPTY, CLOSE, httpClient);
        doTestHttp(endpointAddress, CLOSE, CLOSE, httpClient);
        doTestHttp(endpointAddress, KEEP_ALIVE, KEEP_ALIVE, httpClient);
    }

    private void doTestNoKeepAliveInHttp10(String endpointAddress) throws Exception
    {
        HttpClient httpClient = setupHttpClient(HttpVersion.HTTP_1_0);

        doTestHttp(endpointAddress, EMPTY, CLOSE, httpClient);
        doTestHttp(endpointAddress, CLOSE, CLOSE, httpClient);
        doTestHttp(endpointAddress, KEEP_ALIVE, CLOSE, httpClient);
    }

    private void doTestKeepAliveInHttp11(String endpointAddress) throws Exception
    {
        HttpClient httpClient = setupHttpClient(HttpVersion.HTTP_1_1);

        doTestHttp(endpointAddress, EMPTY, EMPTY, httpClient);
        doTestHttp(endpointAddress, CLOSE, CLOSE, httpClient);
        doTestHttp(endpointAddress, KEEP_ALIVE, EMPTY, httpClient);
    }

    private void doTestNoKeepAliveInHttp11(String endpointAddress) throws Exception
    {
        HttpClient httpClient = setupHttpClient(HttpVersion.HTTP_1_1);

        doTestHttp(endpointAddress, EMPTY, CLOSE, httpClient);
        doTestHttp(endpointAddress, CLOSE, CLOSE, httpClient);
        doTestHttp(endpointAddress, KEEP_ALIVE, CLOSE, httpClient);
    }

    private HttpClient setupHttpClient(HttpVersion version)
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HttpClientParams params = new HttpClientParams();
        //params.setVersion(version);
        //
        //return new HttpClient(params);
        return HttpClients.createDefault();
    }

    private void doTestHttp(String url, String inConnectionHeaderValue, String expectedConnectionHeaderValue, HttpClient httpClient) throws Exception
    {
        HttpGet request = new HttpGet(url);
        if (StringUtils.isEmpty(inConnectionHeaderValue))
        {
            request.removeHeaders(HttpConstants.HEADER_CONNECTION);
        }
        else
        {
            request.setHeader(HttpConstants.HEADER_CONNECTION, inConnectionHeaderValue);
        }

        runHttpMethodAndAssertConnectionHeader(request, expectedConnectionHeaderValue, httpClient);

        // the connection should be still open, send another request and terminate the connection
        request = new HttpGet(url);
        request.setHeader(HttpConstants.HEADER_CONNECTION, CLOSE);
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    private void runHttpMethodAndAssertConnectionHeader(HttpGet request, String expectedConnectionHeaderValue, HttpClient httpClient) throws Exception
    {
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //String connectionHeader;
        //if (httpClient.getParams().getVersion().equals(HttpVersion.HTTP_1_0))
        //{
        //    connectionHeader = response.getFirstHeader(HttpConstants.HEADER_CONNECTION).getValue();
        //    assertNotNull(connectionHeader);
        //}
        //else
        //{
        //    Header responseHeader = request.getResponseHeader(HttpConstants.HEADER_CONNECTION);
        //    connectionHeader = responseHeader != null ? responseHeader.getValue() : EMPTY;
        //}
        //assertEquals(expectedConnectionHeaderValue, connectionHeader);
    }

    private InboundEndpoint getEndpoint(String endpointName)
    {
        return muleContext.getRegistry().lookupObject(endpointName);
    }

    private String getEndpointAddress(String endpointName)
    {
        return getEndpoint(endpointName).getAddress();
    }
}


