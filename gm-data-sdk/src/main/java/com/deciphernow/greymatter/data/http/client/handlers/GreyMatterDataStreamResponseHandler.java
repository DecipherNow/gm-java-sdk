package com.deciphernow.greymatter.data.http.client.handlers;

import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * This requires some more thought, specially for {@link #handleEntity(HttpEntity)} method.
 * The method {@link org.apache.http.impl.client.CloseableHttpClient#execute(HttpHost, HttpRequest, ResponseHandler, HttpContext)} (HttpUriRequest, ResponseHandler, HttpContext)}
 * closes the connection, so when we return an {@link InputStream}, from {@link #handleEntity(HttpEntity)},
 * it throws {@link  java.net.SocketException: Socket is closed error} or {@link java.io.IOException: Attempted read from closed stream}.
 */
@Deprecated
public class GreyMatterDataStreamResponseHandler extends AbstractResponseHandler<InputStream> {
    @Override
    public InputStream handleResponse(final HttpResponse response)
            throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        return entity == null ? null : handleEntity(entity);
    }

    /**
     * Returns the entity as a body as a String.
     */
    @Override
    public InputStream handleEntity(final HttpEntity entity) throws IOException {
        return entity.getContent();
    }
}
