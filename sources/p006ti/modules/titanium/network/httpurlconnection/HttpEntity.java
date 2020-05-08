package p006ti.modules.titanium.network.httpurlconnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* renamed from: ti.modules.titanium.network.httpurlconnection.HttpEntity */
public interface HttpEntity {
    InputStream getContent() throws IOException, IllegalStateException;

    Header getContentEncoding();

    long getContentLength();

    Header getContentType();

    void writeTo(OutputStream outputStream) throws IOException;
}
