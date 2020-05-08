package p006ti.modules.titanium.network.httpurlconnection;

/* renamed from: ti.modules.titanium.network.httpurlconnection.HeaderElement */
public interface HeaderElement {
    String getName();

    NameValuePair getParameter(int i);

    NameValuePair getParameterByName(String str);

    int getParameterCount();

    NameValuePair[] getParameters();

    String getValue();
}
