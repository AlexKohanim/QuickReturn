package p006ti.modules.titanium.network;

import android.os.Build.VERSION;
import android.util.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/* renamed from: ti.modules.titanium.network.TiSocketFactory */
public class TiSocketFactory extends SSLSocketFactory {
    private static final boolean JELLYBEAN_OR_GREATER = (VERSION.SDK_INT >= 16);
    private static final String TAG = "TiSocketFactory";
    private static final String TLS_VERSION_1_0_PROTOCOL = "TLSv1";
    private static final String TLS_VERSION_1_1_PROTOCOL = "TLSv1.1";
    private static final String TLS_VERSION_1_2_PROTOCOL = "TLSv1.2";
    protected String[] enabledProtocols;
    private SSLContext sslContext;
    private String tlsVersion;

    public TiSocketFactory(KeyManager[] keyManagers, TrustManager[] trustManagers, int protocol) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        switch (protocol) {
            case 0:
                break;
            case 1:
                this.tlsVersion = TLS_VERSION_1_0_PROTOCOL;
                this.enabledProtocols = new String[]{TLS_VERSION_1_0_PROTOCOL};
                break;
            case 2:
                this.tlsVersion = TLS_VERSION_1_1_PROTOCOL;
                this.enabledProtocols = new String[]{TLS_VERSION_1_0_PROTOCOL, TLS_VERSION_1_1_PROTOCOL};
                break;
            case 3:
                this.tlsVersion = TLS_VERSION_1_2_PROTOCOL;
                this.enabledProtocols = new String[]{TLS_VERSION_1_0_PROTOCOL, TLS_VERSION_1_1_PROTOCOL, TLS_VERSION_1_2_PROTOCOL};
                break;
            default:
                Log.e(TAG, "Incorrect TLS version was set in HTTPClient. Reverting to default TLS version.");
                break;
        }
        if (JELLYBEAN_OR_GREATER) {
            this.tlsVersion = TLS_VERSION_1_2_PROTOCOL;
            this.enabledProtocols = new String[]{TLS_VERSION_1_0_PROTOCOL, TLS_VERSION_1_1_PROTOCOL, TLS_VERSION_1_2_PROTOCOL};
        } else {
            this.tlsVersion = TLS_VERSION_1_0_PROTOCOL;
            this.enabledProtocols = new String[]{TLS_VERSION_1_0_PROTOCOL};
            Log.i(TAG, this.tlsVersion + " protocol is being used. It is a less-secure version.");
        }
        this.sslContext = SSLContext.getInstance(this.tlsVersion);
        this.sslContext.init(keyManagers, trustManagers, new SecureRandom());
    }

    public String[] getDefaultCipherSuites() {
        return this.enabledProtocols;
    }

    public String[] getSupportedCipherSuites() {
        return this.enabledProtocols;
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return setSupportedAndEnabledProtocolsInSocket(this.enabledProtocols, (SSLSocket) this.sslContext.getSocketFactory().createSocket(host, port));
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return setSupportedAndEnabledProtocolsInSocket(this.enabledProtocols, (SSLSocket) this.sslContext.getSocketFactory().createSocket(host, port, localHost, localPort));
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return setSupportedAndEnabledProtocolsInSocket(this.enabledProtocols, (SSLSocket) this.sslContext.getSocketFactory().createSocket(host, port));
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return setSupportedAndEnabledProtocolsInSocket(this.enabledProtocols, (SSLSocket) this.sslContext.getSocketFactory().createSocket(address, port, localAddress, localPort));
    }

    public Socket createSocket() throws IOException {
        return setSupportedAndEnabledProtocolsInSocket(this.enabledProtocols, (SSLSocket) this.sslContext.getSocketFactory().createSocket());
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return setSupportedAndEnabledProtocolsInSocket(this.enabledProtocols, (SSLSocket) this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose));
    }

    /* access modifiers changed from: protected */
    public SSLSocket setSupportedAndEnabledProtocolsInSocket(String[] enabledProtocols2, SSLSocket sslSocket) {
        String[] supportedProtocols = sslSocket.getSupportedProtocols();
        List<String> supportedAndEnabledProtocols = new ArrayList<>();
        for (String enabledProtocol : enabledProtocols2) {
            int length = supportedProtocols.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String supportedProtocol = supportedProtocols[i];
                if (enabledProtocol.equals(supportedProtocol)) {
                    supportedAndEnabledProtocols.add(supportedProtocol);
                    break;
                }
                i++;
            }
        }
        if (supportedAndEnabledProtocols.size() > 0) {
            sslSocket.setEnabledProtocols((String[]) supportedAndEnabledProtocols.toArray(new String[supportedAndEnabledProtocols.size()]));
        }
        return sslSocket;
    }
}
