package p006ti.modules.titanium.network.httpurlconnection;

import java.io.IOException;
import java.io.OutputStream;

/* renamed from: ti.modules.titanium.network.httpurlconnection.Entity */
public abstract class Entity {
    protected String contentEncoding;
    protected String contentType;

    public abstract void writeTo(OutputStream outputStream) throws IOException;

    public String getContentType() {
        return this.contentType;
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

    public void setContentType(String contentType2) {
        this.contentType = contentType2;
    }

    public void setContentEncoding(String contentEncoding2) {
        this.contentEncoding = contentEncoding2;
    }
}
