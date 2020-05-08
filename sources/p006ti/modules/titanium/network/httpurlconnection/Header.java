package p006ti.modules.titanium.network.httpurlconnection;

import android.net.ParseException;

/* renamed from: ti.modules.titanium.network.httpurlconnection.Header */
public interface Header {
    HeaderElement[] getElements() throws ParseException;

    String getName();

    String getValue();
}
