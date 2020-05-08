package p006ti.modules.titanium.network.httpurlconnection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

/* renamed from: ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils */
public class HttpUrlConnectionUtils {
    public static final String CHARSET_PARAM = "; charset=";
    public static final String CONTENT_TYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
    public static final String ISO_8859_1 = "ISO-8859-1";
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final String PARAMETER_SEPARATOR = "&";
    public static final String PLAIN_TEXT_TYPE = "text/plain";
    public static final String UTF_8 = "UTF-8";

    public static String format(List<? extends NameValuePair> parameters, String encoding) {
        StringBuilder result = new StringBuilder();
        for (NameValuePair parameter : parameters) {
            String encodedName = encode(parameter.getName(), encoding);
            String value = parameter.getValue();
            String encodedValue = value != null ? encode(value, encoding) : "";
            if (result.length() > 0) {
                result.append(PARAMETER_SEPARATOR);
            }
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    private static String encode(String content, String encoding) {
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        try {
            return URLEncoder.encode(content, encoding);
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    public static String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
}
