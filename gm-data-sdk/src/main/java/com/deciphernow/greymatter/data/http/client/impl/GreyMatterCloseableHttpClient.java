package com.deciphernow.greymatter.data.http.client.impl;

import com.deciphernow.greymatter.data.http.client.handlers.*;
import com.deciphernow.greymatter.data.http.client.models.ResponseMetadata;
import com.deciphernow.greymatter.data.http.client.requests.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

public abstract class GreyMatterCloseableHttpClient extends CloseableHttpClient implements HttpClient, Closeable {

    private final Log log = LogFactory.getLog(getClass());

    public ArrayList<ResponseMetadata> execute(final GreyMatterDataMakeDirectoryRequest request) throws IOException {
        return execute(request, new GreyMatterDataMakeDirectoryResponseHandler(), null);
    }

    public ArrayList<ResponseMetadata> execute(final GreyMatterDataListRequest request) throws IOException {
        return execute(request, new GreyMatterDataListResponseHandler(), null);
    }

    public ArrayList<ResponseMetadata> execute(final GreyMatterDataUploadRequest request) throws IOException {
        return execute(request, new GreyMatterDataUploadResponseHandler(), null);
    }

    //TODO: uncomment once {@link GreyMatterDataStreamResponseHandler} is implemented
//    @Deprecated
//    public InputStream execute(final GreyMatterDataStreamRequest request) throws IOException, ClientProtocolException {
//        return execute(request, new GreyMatterDataStreamResponseHandler(), null);
//    }

    public String execute(final GreyMatterDataPurgeRequest request) throws IOException {
        return execute(request, new GreyMatterDataPurgeResponseHandler(), null);
    }

    public ArrayList<ResponseMetadata> execute(final GreyMatterDataDeleteRequest request) throws IOException {
        return execute(request, new GreyMatterDataDeleteResponseHandler(), null);
    }
}
