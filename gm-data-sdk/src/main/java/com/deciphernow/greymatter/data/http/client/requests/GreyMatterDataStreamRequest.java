package com.deciphernow.greymatter.data.http.client.requests;

import com.deciphernow.greymatter.data.http.client.impl.GreyMatterDataEndpoints;
import com.deciphernow.greymatter.data.http.client.settings.GreyMatterClientConfig;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;

public class GreyMatterDataStreamRequest extends HttpRequestBase {

    private final static String METHOD_NAME = "GET";

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public GreyMatterDataStreamRequest(GreyMatterClientConfig config, final String oid) {
        super();
        Validate.notNull(config, "Provided GreyMatterClientConfig is null");
        
        setURI(URI.create(config.getHostName() + ":" +
                config.getPort() +
                config.getPrefix() +
                GreyMatterDataEndpoints.STREAM.getEndpoint() +
                "/" + oid + "/"));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
