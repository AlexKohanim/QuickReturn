package p006ti.modules.titanium.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.appcelerator.kroll.common.Log;

/* renamed from: ti.modules.titanium.network.NonValidatingSSLSocketFactory */
public class NonValidatingSSLSocketFactory extends SSLSocketFactory {
    private static final String TAG = "NVSSLSocketFactory";
    private SSLSocketFactory sslFactory;

    public NonValidatingSSLSocketFactory() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new NonValidatingTrustManager()}, new SecureRandom());
            this.sslFactory = context.getSocketFactory();
        } catch (Exception e) {
            Log.m34e(TAG, e.getMessage(), (Throwable) e);
        }
    }

    public Socket createSocket() throws IOException {
        return this.sslFactory.createSocket();
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return this.sslFactory.createSocket(host, port);
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return this.sslFactory.createSocket(host, port, localHost, localPort);
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return this.sslFactory.createSocket(host, port);
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return this.sslFactory.createSocket(address, port, localAddress, localPort);
    }

    public String[] getDefaultCipherSuites() {
        return null;
    }

    public String[] getSupportedCipherSuites() {
        return null;
    }

    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return this.sslFactory.createSocket(s, host, port, autoClose);
    }
}
