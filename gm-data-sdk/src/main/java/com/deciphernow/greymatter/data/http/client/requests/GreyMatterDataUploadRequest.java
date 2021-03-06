package com.deciphernow.greymatter.data.http.client.requests;

import com.deciphernow.greymatter.data.http.client.impl.GreyMatterDataEndpoints;
import com.deciphernow.greymatter.data.http.client.models.Metadata;
import com.deciphernow.greymatter.data.http.client.settings.GreyMatterClientConfig;
import com.google.gson.Gson;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

public class GreyMatterDataUploadRequest extends HttpEntityEnclosingRequestBase {
    private final static String METHOD_NAME = "POST";

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public GreyMatterDataUploadRequest(GreyMatterClientConfig config, final InputStream stream, final ArrayList<Metadata> metadata) {
        super();
        Validate.notNull(config, "Provided GreyMatterClientConfig is null");
        Validate.notNull(stream, "InputStream is null");
        Validate.notNull(metadata, "Provided metadata is null");

        Gson gson = new Gson();
        String message = gson.toJson(metadata, ArrayList.class);

        StringBody meta = new StringBody(message, ContentType.APPLICATION_JSON);
        InputStreamBody blob = new InputStreamBody(stream, ContentType.DEFAULT_BINARY);

        HttpEntity data = MultipartEntityBuilder.create()
                .setLaxMode()
                .addPart("metadata", meta)
                .addPart("blob", blob)
                .build();

        setEntity(data);

        setURI(URI.create(config.getHostName() + ":" +
                config.getPort() +
                config.getPrefix() +
                GreyMatterDataEndpoints.WRITE.getEndpoint()));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
