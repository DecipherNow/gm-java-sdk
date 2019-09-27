package com.deciphernow.http;

import com.deciphernow.greymatter.data.http.client.GreyMatterDataClient;
import com.deciphernow.greymatter.data.http.client.handlers.GreyMatterDataListResponseHandler;
import com.deciphernow.greymatter.data.http.client.handlers.GreyMatterDataMakeDirectoryResponseHandler;
import com.deciphernow.greymatter.data.http.client.handlers.GreyMatterDataUploadResponseHandler;
import com.deciphernow.greymatter.data.http.client.impl.ClientException;
import com.deciphernow.greymatter.data.http.client.impl.GreyMatterCloseableHttpClient;
import com.deciphernow.greymatter.data.http.client.impl.SSLTrustManagerHelper;
import com.deciphernow.greymatter.data.http.client.models.*;
import com.deciphernow.greymatter.data.http.client.requests.*;
import com.deciphernow.greymatter.data.http.client.settings.GreyMatterClientConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;

public class GreyMatterDataClientTest {
    private static GreyMatterDataClient dataClient;
    private static GreyMatterCloseableHttpClient httpClient;
    private static GreyMatterClientConfig config;
    private static String worldFolder = "";
    private static String userFolder = "";

    @BeforeClass
    public static void getClient() throws Exception {
        String keyStore = "./src/test/resources/keystore.p12";
        String keyStorePassword = "password";
        String trustStore = "./src/test/resources/truststore";
        String trustStorePassword = "password";

        System.setProperty("javax.net.debug", "none"); //TODO: comment out when TLS logs are needed

        SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keyStore, keyStorePassword, trustStore, trustStorePassword);
        config = new GreyMatterClientConfig("https://localhost", 9443, "/services/gm-data/1.0");
        dataClient = new GreyMatterDataClient(trustManagerHelper.clientSSLContext(), config);
        httpClient = dataClient.getClient();

        setWorldFolder();
        setUserFolder();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        GreyMatterDataListRequest request = new GreyMatterDataListRequest(config, worldFolder);
        GreyMatterDataListResponseHandler handler = new GreyMatterDataListResponseHandler();

        ArrayList<ResponseMetadata> response = httpClient.execute(request, handler);

        for (ResponseMetadata r : response) {
            Metadata m = r;
            m.setAction(Action.DELETE);
            ArrayList<Metadata> list = new ArrayList<>();
            list.add(m);

            GreyMatterDataDeleteRequest deleteRequest = new GreyMatterDataDeleteRequest(config, list);

            ArrayList<ResponseMetadata> response1 = httpClient.execute(deleteRequest);
            System.out.println("Deleted: " + response1.get(0).getName() + " -- OID: " + response1.get(0).getOid());
        }
        try {
            File f = new File("./src/test/resources/tmp/");
            FileUtils.deleteDirectory(f);
            dataClient.shutDown();
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    @Test
    public void performCreateUserFolderRequest() throws Exception {
        String name = "engineering@deciphernow.com";
        String action = Action.CREATE;
        String poid = worldFolder;

        GreyMatterDataMakeDirectoryRequest request = new GreyMatterDataMakeDirectoryRequest(config, generateMetadata(name, action, poid, false, ""));

        ArrayList<ResponseMetadata> response = httpClient.execute(request);
        Assert.assertEquals(name, response.get(0).getName());
        userFolder = response.get(0).getOid();

        System.out.println("------------CREATE USER FOLDER DONE------------");
    }

    @Test
    public void performCreateFolderRequest() throws Exception {
        String name = "parth_shah";
        String action = Action.CREATE;
        String poid = userFolder;

        GreyMatterDataMakeDirectoryRequest request = new GreyMatterDataMakeDirectoryRequest(config, generateMetadata(name, action, poid, false, ""));

        ArrayList<ResponseMetadata> response = httpClient.execute(request);
        Assert.assertEquals(name, response.get(0).getName());

        System.out.println("------------CREATE FOLDER DONE------------");
    }

    @Test
    public void performClientUploadSmallRequest() throws Exception {
        InputStream stream = new FileInputStream(new File("./src/test/resources/sample.txt"));
        String name = "sample.txt";
        String action = Action.CREATE;
        String mime = "plain/text";

        GreyMatterDataUploadRequest request = new GreyMatterDataUploadRequest(config, stream, generateMetadata(name, action, userFolder, true, mime));

        ArrayList<ResponseMetadata> response = httpClient.execute(request);
        Assert.assertEquals(name, response.get(0).getName());

        System.out.println("------------UPLOAD SMALL FILE DONE------------");
    }

    @Test
    public void performClientUploadMediumRequest() throws Exception {
        InputStream stream = new FileInputStream(new File(("./src/test/resources/big_ben.jpg")));

        String name = "big_ben.jpg";
        String action = Action.CREATE;
        String mime = "image/jpeg";

        GreyMatterDataUploadRequest request = new GreyMatterDataUploadRequest(config, stream, generateMetadata(name, action, userFolder, true, mime));

        ArrayList<ResponseMetadata> response = httpClient.execute(request);
        Assert.assertEquals(name, response.get(0).getName());

        System.out.println("------------UPLOAD MEDIUM FILE DONE------------");
    }

    @Ignore
    public void performClientUploadLargeRequest() throws Exception {
        //TODO: add large file while running this test and change the file path on a next line
        InputStream stream = new FileInputStream(new File(("./src/test/resources/video.mp4")));

        String name = "video.mp4";
        String action = Action.CREATE;
        String mime = "video/mp4";

        GreyMatterDataUploadRequest request = new GreyMatterDataUploadRequest(config, stream, generateMetadata(name, action, userFolder, true, mime));

        ArrayList<ResponseMetadata> response = httpClient.execute(request);
        Assert.assertEquals(name, response.get(0).getName());

        System.out.println("------------UPLOAD LARGE FILE DONE------------");
    }

    @Test
    public void performValidClientStreamRequest() throws Exception {
        String oid = uploadMediumFile();
        GreyMatterDataStreamRequest request = new GreyMatterDataStreamRequest(config, oid);
        //TODO: implement {@link GreyMatterDataStreamResponseHandler}
        HttpResponse response = httpClient.execute(request);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();

        File directory = new File("./src/test/resources/tmp");

        if (!directory.exists()) {
            directory.mkdir();
        }

        File targetFile = new File("./src/test/resources/tmp/image.jpg");
        OutputStream outStream = new FileOutputStream(targetFile);

        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = entity.getContent().read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        IOUtils.closeQuietly(entity.getContent());
        IOUtils.closeQuietly(outStream);

        System.out.println("------------STREAM FILE DONE------------");
    }

    @Test
    public void performClientListRequest() throws Exception {
        uploadMediumFile();
        GreyMatterDataListRequest request = new GreyMatterDataListRequest(config, userFolder);
        GreyMatterDataListResponseHandler handler = new GreyMatterDataListResponseHandler();

        ArrayList<ResponseMetadata> response = httpClient.execute(request, handler);

        Assert.assertEquals(userFolder, response.get(0).getParentoid());

        System.out.println("------------LISTING DONE------------");
    }

    private String uploadMediumFile() throws Exception {
        System.out.println("Uploading image file...");
        InputStream stream = new FileInputStream(new File(("./src/test/resources/big_ben.jpg")));

        String name = "big_ben.jpg";
        String action = Action.CREATE;
        String mime = "image/jpeg";

        GreyMatterDataUploadRequest request = new GreyMatterDataUploadRequest(config, stream, generateMetadata(name, action, userFolder, true, mime));
        GreyMatterDataUploadResponseHandler handler = new GreyMatterDataUploadResponseHandler();

        ArrayList<ResponseMetadata> response = httpClient.execute(request, handler);
        System.out.println("Done uploading image file...");
        return response.get(0).getOid();
    }

    private static void setWorldFolder() throws Exception {
        GreyMatterDataListRequest request = new GreyMatterDataListRequest(config, "0000000000000001");
        GreyMatterDataListResponseHandler handler = new GreyMatterDataListResponseHandler();

        ArrayList<ResponseMetadata> response = httpClient.execute(request, handler);
        worldFolder = response.get(0).getOid();
    }

    private static void setUserFolder() throws Exception {
        String name = "engineering@deciphernow.com";
        String action = Action.CREATE;
        String poid = worldFolder;

        GreyMatterDataMakeDirectoryRequest request = new GreyMatterDataMakeDirectoryRequest(config, generateMetadata(name, action, poid, false, ""));
        GreyMatterDataMakeDirectoryResponseHandler handler = new GreyMatterDataMakeDirectoryResponseHandler();

        ArrayList<ResponseMetadata> response = httpClient.execute(request, handler);
        userFolder = response.get(0).getOid();
    }

    private static Security generateSecurity() {
        return new Security("DECIPHER//GMDATA", "#FFFFFF", "green");
    }

    private static ObjectPolicy generateObjectPolicy() {
        return new ObjectPolicy("forAnonReadRobFull", new Requirements("yield-all"));
    }

    private static ArrayList<Metadata> generateMetadata(String name, String action, String poid, boolean isFile, String mimeType) {
        ArrayList<Metadata> metadata = new ArrayList<>();
        Metadata meta = new Metadata(name, action, poid, isFile, mimeType, generateSecurity(), generateObjectPolicy());
        metadata.add(meta);
        return metadata;
    }
}
