package p006ti.modules.titanium.network;

import android.net.Uri;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

/* renamed from: ti.modules.titanium.network.SecurityManagerProtocol */
public interface SecurityManagerProtocol {
    X509KeyManager[] getKeyManagers(HTTPClientProxy hTTPClientProxy);

    X509TrustManager[] getTrustManagers(HTTPClientProxy hTTPClientProxy);

    boolean willHandleURL(Uri uri);
}
