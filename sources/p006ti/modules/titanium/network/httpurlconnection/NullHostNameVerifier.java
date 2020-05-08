package p006ti.modules.titanium.network.httpurlconnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/* renamed from: ti.modules.titanium.network.httpurlconnection.NullHostNameVerifier */
public class NullHostNameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
