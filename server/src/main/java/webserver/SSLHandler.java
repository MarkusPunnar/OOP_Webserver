package webserver;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

public class SSLHandler {

    public ServerSocket getSSLHandler(int portNumber) throws Exception {
        final String keyStoreType = "JKS";
        final String keyStorePassword = "password";
        TrustManagerFactory tmFactory;
        KeyManagerFactory keyFactory;
        try (InputStream is = WebServer.class.getClassLoader().getResourceAsStream("keystore.jks")) {
            tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore trustStore = KeyStore.getInstance(keyStoreType);
            trustStore.load(is, keyStorePassword.toCharArray());
            tmFactory.init(trustStore);
        }
        try (InputStream newStream = WebServer.class.getClassLoader().getResourceAsStream("keystore.jks")) {
            keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore ksStore = KeyStore.getInstance(keyStoreType);
            ksStore.load(newStream, keyStorePassword.toCharArray());
            keyFactory.init(ksStore, keyStorePassword.toCharArray());
        }
        KeyManager[] managers = keyFactory.getKeyManagers();
        TrustManager[] trustManagers = tmFactory.getTrustManagers();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(managers, trustManagers, null);
        return sslContext.getServerSocketFactory().createServerSocket(portNumber);
    }
}
