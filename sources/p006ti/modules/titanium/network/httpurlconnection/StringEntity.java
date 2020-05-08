package p006ti.modules.titanium.network.httpurlconnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/* renamed from: ti.modules.titanium.network.httpurlconnection.StringEntity */
public class StringEntity extends Entity {
    protected final byte[] content;

    public StringEntity(String s, String mimeType, String charset) throws UnsupportedEncodingException {
        if (s == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        if (mimeType == null) {
            mimeType = HttpUrlConnectionUtils.PLAIN_TEXT_TYPE;
        }
        if (charset == null) {
            charset = "ISO-8859-1";
        }
        this.content = s.getBytes(charset);
        setContentType(mimeType + HttpUrlConnectionUtils.CHARSET_PARAM + charset);
    }

    public StringEntity(String s, String charset) throws UnsupportedEncodingException {
        this(s, null, charset);
    }

    public StringEntity(String s) throws UnsupportedEncodingException {
        this(s, null);
    }

    public boolean isRepeatable() {
        return true;
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
