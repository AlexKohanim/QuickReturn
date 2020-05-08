package p006ti.modules.titanium.network;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/* renamed from: ti.modules.titanium.network.NonValidatingTrustManager */
public class NonValidatingTrustManager implements X509TrustManager {
    private static final X509Certificate[] certs = new X509Certificate[0];

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return certs;
    }
}
