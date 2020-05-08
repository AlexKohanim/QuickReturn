package p006ti.modules.titanium.network;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.network.CookieProxy */
public class CookieProxy extends KrollProxy {
    private static final String TAG = "CookieProxy";
    private static final SimpleDateFormat httpExpiryDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final SimpleDateFormat systemExpiryDateFormatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'");
    private static TimeZone timezone = TimeZone.getTimeZone("GMT");
    private HttpCookie httpCookie;

    static {
        httpExpiryDateFormatter.setTimeZone(timezone);
        systemExpiryDateFormatter.setTimeZone(timezone);
    }

    public CookieProxy() {
    }

    public CookieProxy(HttpCookie cookie) {
        if (cookie instanceof HttpCookie) {
            this.httpCookie = cookie;
            setProperty(TiC.PROPERTY_NAME, this.httpCookie.getName());
            setProperty(TiC.PROPERTY_VALUE, this.httpCookie.getValue());
            setProperty(TiC.PROPERTY_DOMAIN, this.httpCookie.getDomain());
            setProperty(TiC.PROPERTY_MAX_AGE, Long.valueOf(this.httpCookie.getMaxAge()));
            setProperty(TiC.PROPERTY_COMMENT, this.httpCookie.getComment());
            setProperty(TiC.PROPERTY_PATH, this.httpCookie.getPath());
            setProperty(TiC.PROPERTY_SECURE, Boolean.valueOf(this.httpCookie.getSecure()));
            setProperty(TiC.PROPERTY_VERSION, Integer.valueOf(this.httpCookie.getVersion()));
            return;
        }
        Log.m32e(TAG, "Unable to create CookieProxy. Invalid cookie type.");
    }

    public CookieProxy(String name, String value, String domain, String path) {
        KrollDict dict = new KrollDict();
        dict.put(TiC.PROPERTY_NAME, name);
        dict.put(TiC.PROPERTY_VALUE, value);
        setProperty(TiC.PROPERTY_NAME, name);
        setProperty(TiC.PROPERTY_VALUE, value);
        if (domain != null) {
            dict.put(TiC.PROPERTY_DOMAIN, domain);
            setProperty(TiC.PROPERTY_DOMAIN, domain);
        }
        if (path != null) {
            dict.put(TiC.PROPERTY_PATH, path);
            setProperty(TiC.PROPERTY_PATH, path);
        }
        handleCreationDict(dict);
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        String name = TiConvert.toString(getProperty(TiC.PROPERTY_NAME));
        String value = TiConvert.toString(getProperty(TiC.PROPERTY_VALUE));
        if (name != null) {
            this.httpCookie = new HttpCookie(name, value);
            if (dict.containsKey(TiC.PROPERTY_DOMAIN)) {
                this.httpCookie.setDomain(TiConvert.toString(getProperty(TiC.PROPERTY_DOMAIN)));
            }
            if (dict.containsKey(TiC.PROPERTY_MAX_AGE)) {
                this.httpCookie.setMaxAge((long) TiConvert.toInt(getProperty(TiC.PROPERTY_MAX_AGE)));
            }
            if (dict.containsKey(TiC.PROPERTY_COMMENT)) {
                this.httpCookie.setComment(TiConvert.toString(getProperty(TiC.PROPERTY_COMMENT)));
            }
            if (dict.containsKey(TiC.PROPERTY_PATH)) {
                this.httpCookie.setPath(TiConvert.toString(getProperty(TiC.PROPERTY_PATH)));
            }
            if (dict.containsKey(TiC.PROPERTY_SECURE)) {
                this.httpCookie.setSecure(TiConvert.toBoolean(getProperty(TiC.PROPERTY_SECURE)));
            }
            if (dict.containsKey(TiC.PROPERTY_VERSION)) {
                this.httpCookie.setVersion(TiConvert.toInt(getProperty(TiC.PROPERTY_VERSION)));
                return;
            }
            return;
        }
        Log.m44w(TAG, "Unable to create the http client cookie. Need to provide a valid name.");
    }

    public void onPropertyChanged(String name, Object value) {
        if (this.httpCookie != null) {
            super.onPropertyChanged(name, value);
            if (TiC.PROPERTY_VALUE.equals(name)) {
                this.httpCookie.setValue(TiConvert.toString(value));
            } else if (TiC.PROPERTY_DOMAIN.equals(name)) {
                this.httpCookie.setDomain(TiConvert.toString(value));
            } else if (TiC.PROPERTY_MAX_AGE.equals(name)) {
                this.httpCookie.setMaxAge((long) TiConvert.toInt(value));
            } else if (TiC.PROPERTY_COMMENT.equals(name)) {
                this.httpCookie.setComment(TiConvert.toString(value));
            } else if (TiC.PROPERTY_PATH.equals(name)) {
                this.httpCookie.setPath(TiConvert.toString(value));
            } else if (TiC.PROPERTY_SECURE.equals(name)) {
                this.httpCookie.setSecure(TiConvert.toBoolean(value));
            } else if (TiC.PROPERTY_VERSION.equals(name)) {
                this.httpCookie.setVersion(TiConvert.toInt(value));
            }
        }
    }

    public HttpCookie getHTTPCookie() {
        return this.httpCookie;
    }

    public String getName() {
        return TiConvert.toString(getProperty(TiC.PROPERTY_NAME));
    }

    public String getApiName() {
        return "Ti.Network.Cookie";
    }
}
