package com.deciphernow.greymatter.data.http.client.handlers;

import com.deciphernow.greymatter.data.http.client.models.ResponseMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class GreyMatterDataListResponseHandler extends AbstractResponseHandler<ArrayList<ResponseMetadata>> {
    @Override
    public ArrayList<ResponseMetadata> handleResponse(final HttpResponse response)
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
    public ArrayList<ResponseMetadata> handleEntity(final HttpEntity entity) throws IOException {
        String entityString = EntityUtils.toString(entity);
        Gson gson = new Gson();

        return gson.fromJson(entityString, new TypeToken<ArrayList<ResponseMetadata>>() {
        }.getType());
    }
}
