# Grey Matter Data SDK
## Purpose

This is a Java 8 based SDK designed to interact with `Grey Matter Data` API.

## Requirements
In order to interact with `Grey Matter Data` API, user needs to provide `p12` certificate, `p12` certificate password, `TrustStore`, and `TrustStore` password.

Example of how to get an instance of Grey Matter Data Client

```java
public class MyClient {
    private GreyMatterClientConfig config;
    public GreyMatterCloseableHttpClient getClient() throws Exception {
            String keyStore = "path to p12 file";
            String keyStorePassword = "keystore password";
            String trustStore = "path to TrustStore";
            String trustStorePassword = "TrustStore password";
            
            SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keyStore, keyStorePassword, trustStore, trustStorePassword);
            config = new GreyMatterClientConfig("https://localhost", 9443, "/services/gm-data/1.0");
            GreyMatterDataClient dataClient = new GreyMatterDataClient(trustManagerHelper.clientSSLContext(), config);
            return dataClient.getClient();
    }
}
```

`SSLTrustManagerHelper` class can handle `file_path` as a `String`, `File` object, or an `InputStream` for `keyStore` and `trustStore`.

## Use case

#### Create a folder
In order to create a folder inside `Grey Matter Data` user can leverage `GreyMatterDataMakeDirectoryRequest`.

Example
```java
public class MyClient {
    private GreyMatterClientConfig config;
    public GreyMatterCloseableHttpClient getClient() throws Exception {
            String keyStore = "path to p12 file";
            String keyStorePassword = "keystore password";
            String trustStore = "path to TrustStore";
            String trustStorePassword = "TrustStore password";
            
            SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keyStore, keyStorePassword, trustStore, trustStorePassword);
            config = new GreyMatterClientConfig("https://localhost", 9443, "/services/gm-data/1.0");
            GreyMatterDataClient dataClient = new GreyMatterDataClient(trustManagerHelper.clientSSLContext(), config);
            return dataClient.getClient();
    }
    private void createFolder() throws Exception {
            String name = "folder_name"; //name of the folder
            String action = Action.CREATE;
            String poid = "parent_oid"; //oid of a folder under which you want to create a folder
    
            GreyMatterDataMakeDirectoryRequest request = new GreyMatterDataMakeDirectoryRequest(config, generateMetadata(name, action, poid, false, ""));
    
            ArrayList<ResponseMetadata> response = httpClient.execute(request);
    }
     private static ArrayList<Metadata> generateMetadata(String name, String action, String poid, boolean isFile, String mimeType) {
            ArrayList<Metadata> metadata = new ArrayList<>();
            Metadata meta = new Metadata(name, action, poid, isFile, mimeType, generateSecurity(), generateObjectPolicy());
            metadata.add(meta);
            return metadata;
    }
}
```

#### Upload a file
In order to upload a file to a folder inside `Grey Matter Data` user can leverage `GreyMatterDataUploadRequest`.

Example
```java
public class MyClient {
    private GreyMatterClientConfig config;
    public GreyMatterCloseableHttpClient getClient() throws Exception {
            String keyStore = "path to p12 file";
            String keyStorePassword = "keystore password";
            String trustStore = "path to TrustStore";
            String trustStorePassword = "TrustStore password";
            
            SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keyStore, keyStorePassword, trustStore, trustStorePassword);
            config = new GreyMatterClientConfig("https://localhost", 9443, "/services/gm-data/1.0");
            GreyMatterDataClient dataClient = new GreyMatterDataClient(trustManagerHelper.clientSSLContext(), config);
            return dataClient.getClient();
    }
    private void uploadFile() throws Exception {
            InputStream stream = new FileInputStream("File as an InputStream");
            String name = "sample.txt"; //file_name (can be 
            String action = Action.CREATE;
            String mime = "plain/text";
            
            GreyMatterDataUploadRequest request = new GreyMatterDataUploadRequest(config, stream, generateMetadata(name, action, userFolder, true, mime));
            
            ArrayList<ResponseMetadata> response = httpClient.execute(request);
    }
     private static ArrayList<Metadata> generateMetadata(String name, String action, String poid, boolean isFile, String mimeType) {
            ArrayList<Metadata> metadata = new ArrayList<>();
            Metadata meta = new Metadata(name, action, poid, isFile, mimeType, generateSecurity(), generateObjectPolicy());
            metadata.add(meta);
            return metadata;
    }
}
```

#### Stream a file
In order to stream a file from `Grey Matter Data` user can leverage `GreyMatterDataStreamRequest`.

Example
```java
public class MyClient {
    private GreyMatterClientConfig config;
    public GreyMatterCloseableHttpClient getClient() throws Exception {
            String keyStore = "path to p12 file";
            String keyStorePassword = "keystore password";
            String trustStore = "path to TrustStore";
            String trustStorePassword = "TrustStore password";
            
            SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keyStore, keyStorePassword, trustStore, trustStorePassword);
            config = new GreyMatterClientConfig("https://localhost", 9443, "/services/gm-data/1.0");
            GreyMatterDataClient dataClient = new GreyMatterDataClient(trustManagerHelper.clientSSLContext(), config);
            return dataClient.getClient();
    }
    public void streamFile() throws Exception {
            String oid = "oid of the file";
            GreyMatterDataStreamRequest request = new GreyMatterDataStreamRequest(config, oid);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
       
            File targetFile = new File("file location");
            OutputStream outStream = new FileOutputStream(targetFile);
       
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = entity.getContent().read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            IOUtils.closeQuietly(entity.getContent());
            IOUtils.closeQuietly(outStream);
    }
}
```

#### List files and directories
In order to list files and directories within a particular folder inside `Grey Matter Data` user can leverage `GreyMatterDataListRequest`.

Example
```java
public class MyClient {
    private GreyMatterClientConfig config;
    public GreyMatterCloseableHttpClient getClient() throws Exception {
            String keyStore = "path to p12 file";
            String keyStorePassword = "keystore password";
            String trustStore = "path to TrustStore";
            String trustStorePassword = "TrustStore password";
            
            SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keyStore, keyStorePassword, trustStore, trustStorePassword);
            config = new GreyMatterClientConfig("https://localhost", 9443, "/services/gm-data/1.0");
            GreyMatterDataClient dataClient = new GreyMatterDataClient(trustManagerHelper.clientSSLContext(), config);
            return dataClient.getClient();
    }
    public void getList() throws Exception {
            String oid = "oid of the file";
            GreyMatterDataListRequest request = new GreyMatterDataListRequest(config, oid);
            GreyMatterDataListResponseHandler handler = new GreyMatterDataListResponseHandler();
            
            ArrayList<ResponseMetadata> response = httpClient.execute(request, handler);
    }
}
```

#### Delete a file or a folder
In order to delete a file or folder inside `Grey Matter Data` user can leverage `GreyMatterDataDeleteRequest`.

Example
```java
public class MyClient {
    private GreyMatterClientConfig config;
    public GreyMatterCloseableHttpClient getClient() throws Exception {
            String keyStore = "path to p12 file";
            String keyStorePassword = "keystore password";
            String trustStore = "path to TrustStore";
            String trustStorePassword = "TrustStore password";
            
            SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keyStore, keyStorePassword, trustStore, trustStorePassword);
            config = new GreyMatterClientConfig("https://localhost", 9443, "/services/gm-data/1.0");
            GreyMatterDataClient dataClient = new GreyMatterDataClient(trustManagerHelper.clientSSLContext(), config);
            return dataClient.getClient();
    }
    public void deleteFolder() throws Exception {
            String oid = "oid of the folder";
            GreyMatterDataListRequest request = new GreyMatterDataListRequest(config, oid); //create a list request object for the files and folder within the folder
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
                dataClient.shutDown();
            } catch (IOException e) {
                throw new ClientException(e);
            }
    }
}
```
