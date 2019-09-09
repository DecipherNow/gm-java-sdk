package com.deciphernow.greymatter.data.http.client;

import com.deciphernow.greymatter.data.http.client.impl.GreyMatterCloseableHttpClient;
import com.deciphernow.greymatter.data.http.client.impl.GreyMatterHttpClientBuilder;
import com.deciphernow.greymatter.data.http.client.settings.GreyMatterClientConfig;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;

public class GreyMatterDataClient extends GreyMatterHttpClientBuilder {
    static final Log LOG = LogFactory.getLog(GreyMatterDataClient.class);

    private static final GreyMatterHttpClientBuilder builder = GreyMatterHttpClientBuilder.create();

    private GreyMatterCloseableHttpClient httpClient;
    private GreyMatterClientConfig config;

    public GreyMatterDataClient(SSLContext sslContext, GreyMatterClientConfig config) {
        Validate.notNull(sslContext, "Provided sslContext is null");
        Validate.notNull(config, "Provided GreyMatterClientConfig is null");

        this.httpClient = builder.setSSLContext(sslContext)
                .setUserAgent(config.getUserAgentPrefix())
                .build();
        this.config = config;
    }

    public GreyMatterCloseableHttpClient getClient() {
        return this.httpClient;
    }

    public GreyMatterClientConfig getConfig() {
        return this.config;
    }

    public void shutDown() throws IOException {
        httpClient.close();
    }
}
