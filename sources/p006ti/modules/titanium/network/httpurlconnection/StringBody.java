package p006ti.modules.titanium.network.httpurlconnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.titanium.TiC;

/* renamed from: ti.modules.titanium.network.httpurlconnection.StringBody */
public class StringBody extends AbstractContentBody {
    private final Charset charset;
    private final byte[] content;

    public StringBody(String text, String mimeType, Charset charset2) throws UnsupportedEncodingException {
        super(mimeType);
        if (text == null) {
            throw new IllegalArgumentException("Text may not be null");
        }
        if (charset2 == null) {
            charset2 = Charset.defaultCharset();
        }
        this.content = text.getBytes(charset2.name());
        this.charset = charset2;
    }

    public StringBody(String text, Charset charset2) throws UnsupportedEncodingException {
        this(text, HttpUrlConnectionUtils.PLAIN_TEXT_TYPE, charset2);
    }

    public StringBody(String text) throws UnsupportedEncodingException {
        this(text, HttpUrlConnectionUtils.PLAIN_TEXT_TYPE, null);
    }

    public Reader getReader() {
        return new InputStreamReader(new ByteArrayInputStream(this.content), this.charset);
    }

    public void writeTo(OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new ByteArrayInputStream(this.content);
        byte[] tmp = new byte[4096];
        while (true) {
            int l = in.read(tmp);
            if (l != -1) {
                out.write(tmp, 0, l);
            } else {
                out.flush();
                return;
            }
        }
    }

    public String getTransferEncoding() {
        return "8bit";
    }

    public String getCharset() {
        return this.charset.name();
    }

    public Map<String, String> getContentTypeParameters() {
        Map<String, String> map = new HashMap<>();
        map.put(TiC.PROPERTY_CHARSET, this.charset.name());
        return map;
    }

    public long getContentLength() {
        return (long) this.content.length;
    }

    public String getFilename() {
        return null;
    }
}
