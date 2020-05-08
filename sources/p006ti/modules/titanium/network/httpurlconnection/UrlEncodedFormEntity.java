package p006ti.modules.titanium.network.httpurlconnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/* renamed from: ti.modules.titanium.network.httpurlconnection.UrlEncodedFormEntity */
public class UrlEncodedFormEntity extends Entity {
    protected final byte[] content;

    public UrlEncodedFormEntity(List<? extends NameValuePair> parameters, String encoding) throws UnsupportedEncodingException {
        this(HttpUrlConnectionUtils.format(parameters, encoding), encoding);
        StringBuilder append = new StringBuilder().append("application/x-www-form-urlencoded; charset=");
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        setContentType(append.append(encoding).toString());
    }

    public UrlEncodedFormEntity(List<? extends NameValuePair> parameters) throws UnsupportedEncodingException {
        this(parameters, "ISO-8859-1");
    }

    public UrlEncodedFormEntity(String s, String charset) throws UnsupportedEncodingException {
        if (s == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        if (charset == null) {
            charset = "ISO-8859-1";
        }
        this.content = s.getBytes(charset);
        setContentType("text/plain; charset=" + charset);
    }

    public UrlEncodedFormEntity(String s) throws UnsupportedEncodingException {
        this(s, (String) null);
    }

    public long getContentLength() {
        return (long) this.content.length;
    }

    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        outstream.write(this.content);
        outstream.flush();
    }
}
