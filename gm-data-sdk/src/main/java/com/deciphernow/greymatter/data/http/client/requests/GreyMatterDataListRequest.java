package com.deciphernow.greymatter.data.http.client.requests;

import com.deciphernow.greymatter.data.http.client.impl.GreyMatterDataEndpoints;
import com.deciphernow.greymatter.data.http.client.settings.GreyMatterClientConfig;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;

public class GreyMatterDataListRequest extends HttpRequestBase {
    private final static String METHOD_NAME = "GET";

    public GreyMatterDataListRequest(GreyMatterClientConfig config, final String oid) {
        super();
        Validate.notNull(config, "Provided GreyMatterClientConfig is null");
        Validate.notBlank(oid, "Provided oid is null");

        setURI(URI.create(config.getHostName() + ":" +
                config.getPort() +
                config.getPrefix() +
                GreyMatterDataEndpoints.LIST.getEndpoint() +
                "/" + oid + "/"));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
