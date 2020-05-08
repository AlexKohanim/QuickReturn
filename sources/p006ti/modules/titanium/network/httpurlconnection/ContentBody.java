package p006ti.modules.titanium.network.httpurlconnection;

import java.io.IOException;
import java.io.OutputStream;

/* renamed from: ti.modules.titanium.network.httpurlconnection.ContentBody */
public interface ContentBody extends ContentDescriptor {
    String getFilename();

    void writeTo(OutputStream outputStream) throws IOException;
}
