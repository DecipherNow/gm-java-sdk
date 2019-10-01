package com.deciphernow.greymatter.data.http.client.impl;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class SSLTrustManagerHelper {
    private InputStream keyStore;
    private String keyStorePassword;
    private InputStream trustStore;
    private String trustStorePassword;

    public SSLTrustManagerHelper(String keyStore,
                                 String keyStorePassword,
                                 String trustStore,
                                 String trustStorePassword) throws FileNotFoundException {

        this.keyStore = new FileInputStream(new File(keyStore));
        this.keyStorePassword = keyStorePassword;
        this.trustStore = new FileInputStream(new File(trustStore));
        this.trustStorePassword = trustStorePassword;
    }

    public SSLTrustManagerHelper(File keyStore,
                                 String keyStorePassword,
                                 File trustStore,
                                 String trustStorePassword) throws FileNotFoundException {


        this.keyStore = new FileInputStream(keyStore);
        this.keyStorePassword = keyStorePassword;
        this.trustStore = new FileInputStream(trustStore);
        this.trustStorePassword = trustStorePassword;
    }

    public SSLTrustManagerHelper(InputStream keyStore,
                                 String keyStorePassword,
                                 InputStream trustStore,
                                 String trustStorePassword) {
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
    }

    public SSLContext clientSSLContext() {
        try {
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(trustStore, trustStorePassword);

            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keyStore, keyStorePassword);

            return getSSLContext(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | KeyManagementException e) {
            throw new ClientException(e);
        }
    }

    private static SSLContext getSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private static KeyManagerFactory getKeyManagerFactory(InputStream keystorePath, String keystorePassword) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

        keystore.load(keystorePath, keystorePassword.toCharArray());

        keyManagerFactory.init(keystore, keystorePassword.toCharArray());
        return keyManagerFactory;
    }

    private static TrustManagerFactory getTrustManagerFactory(InputStream truststorePath, String truststorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        trustStore.load(truststorePath, truststorePassword.toCharArray());

        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }
}
