package p006ti.modules.titanium.network;

import android.os.Build.VERSION;
import java.io.UnsupportedEncodingException;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.xml.DocumentProxy;

/* renamed from: ti.modules.titanium.network.HTTPClientProxy */
public class HTTPClientProxy extends KrollProxy {
    public static final int DONE = 4;
    public static final int HEADERS_RECEIVED = 2;
    private static final boolean JELLYBEAN_OR_GREATER = (VERSION.SDK_INT >= 16);
    public static final int LOADING = 3;
    public static final int OPENED = 1;
    public static final String PROPERTY_SECURITY_MANAGER = "securityManager";
    private static final String TAG = "TiHTTPClientProxy";
    public static final int UNSENT = 0;
    private TiHTTPClient client = new TiHTTPClient(this);

    public void release() {
        this.client = null;
        super.release();
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        if (hasProperty(TiC.PROPERTY_TIMEOUT)) {
            this.client.setTimeout(TiConvert.toInt(getProperty(TiC.PROPERTY_TIMEOUT), 0));
        }
        if (hasProperty(TiC.PROPERTY_AUTO_REDIRECT)) {
            this.client.setAutoRedirect(TiConvert.toBoolean(getProperty(TiC.PROPERTY_AUTO_REDIRECT), true));
        }
        if (hasProperty(TiC.PROPERTY_AUTO_ENCODE_URL)) {
            this.client.setAutoEncodeUrl(TiConvert.toBoolean(getProperty(TiC.PROPERTY_AUTO_ENCODE_URL), true));
        }
        if (hasProperty(PROPERTY_SECURITY_MANAGER)) {
            Object prop = getProperty(PROPERTY_SECURITY_MANAGER);
            if (prop != null) {
                if (prop instanceof SecurityManagerProtocol) {
                    this.client.securityManager = (SecurityManagerProtocol) prop;
                } else {
                    throw new IllegalArgumentException("Invalid argument passed to securityManager property. Does not conform to SecurityManagerProtocol");
                }
            }
        }
        this.client.setTlsVersion(TiConvert.toInt(getProperty(TiC.PROPERTY_TLS_VERSION), 0));
    }

    public void abort() {
        this.client.abort();
    }

    public String getAllResponseHeaders() {
        return this.client.getAllResponseHeaders();
    }

    public int getReadyState() {
        return this.client.getReadyState();
    }

    public TiBlob getResponseData() {
        return this.client.getResponseData();
    }

    public String getResponseHeader(String header) {
        return this.client.getResponseHeader(header);
    }

    public String getResponseText() {
        return this.client.getResponseText();
    }

    public DocumentProxy getResponseXML() {
        return this.client.getResponseXML();
    }

    public int getStatus() {
        return this.client.getStatus();
    }

    public String getStatusText() {
        return this.client.getStatusText();
    }

    public void open(String method, String url) {
        this.client.open(method, url);
    }

    public void send(@argument(optional = true) Object data) throws UnsupportedEncodingException {
        this.client.send(data);
    }

    public void clearCookies(String host) {
        this.client.clearCookies(host);
    }

    public void setRequestHeader(String header, String value) {
        this.client.setRequestHeader(header, value);
    }

    public void setTimeout(int millis) {
        this.client.setTimeout(millis);
    }

    public String getLocation() {
        return this.client.getLocation();
    }

    public String getConnectionType() {
        return this.client.getConnectionType();
    }

    public boolean getConnected() {
        return this.client.isConnected();
    }

    public boolean getAutoEncodeUrl() {
        return this.client.getAutoEncodeUrl();
    }

    public void setAutoEncodeUrl(boolean value) {
        this.client.setAutoEncodeUrl(value);
    }

    public boolean getAutoRedirect() {
        return this.client.getAutoRedirect();
    }

    public void setAutoRedirect(boolean value) {
        this.client.setAutoRedirect(value);
    }

    public boolean getValidatesSecureCertificate() {
        return this.client.validatesSecureCertificate();
    }

    public void setValidatesSecureCertificate(boolean value) {
        setProperty("validatesSecureCertificate", Boolean.valueOf(value));
    }

    public void setUsername(String value) {
        setProperty(TiC.PROPERTY_USERNAME, value);
    }

    public String getUsername() {
        if (hasProperty(TiC.PROPERTY_USERNAME)) {
            return TiConvert.toString(getProperty(TiC.PROPERTY_USERNAME));
        }
        return null;
    }

    public void setPassword(String value) {
        setProperty(TiC.PROPERTY_PASSWORD, value);
    }

    public String getPassword() {
        if (hasProperty(TiC.PROPERTY_PASSWORD)) {
            return TiConvert.toString(getProperty(TiC.PROPERTY_PASSWORD));
        }
        return null;
    }

    public void setDomain(String value) {
        setProperty(TiC.PROPERTY_DOMAIN, value);
    }

    public String getDomain() {
        if (hasProperty(TiC.PROPERTY_DOMAIN)) {
            return TiConvert.toString(getProperty(TiC.PROPERTY_DOMAIN));
        }
        return null;
    }

    public void addTrustManager(Object manager) {
        if (manager instanceof X509TrustManager) {
            this.client.addTrustManager((X509TrustManager) manager);
        }
    }

    public void addKeyManager(Object manager) {
        if (manager instanceof X509KeyManager) {
            this.client.addKeyManager((X509KeyManager) manager);
        }
    }

    public void setTlsVersion(int tlsVersion) {
        this.client.setTlsVersion(tlsVersion);
    }

    public int getTlsVersion() {
        if (!hasProperty(TiC.PROPERTY_TLS_VERSION)) {
            return 0;
        }
        int tlsVersion = TiConvert.toInt(getProperty(TiC.PROPERTY_TLS_VERSION));
        if (tlsVersion != 0) {
            return tlsVersion;
        }
        if (JELLYBEAN_OR_GREATER) {
            return 3;
        }
        return 1;
    }

    public String getApiName() {
        return "Ti.Network.HTTPClient";
    }
}
