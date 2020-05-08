package p006ti.modules.titanium.network.httpurlconnection;

/* renamed from: ti.modules.titanium.network.httpurlconnection.ContentDescriptor */
public interface ContentDescriptor {
    String getCharset();

    long getContentLength();

    String getMediaType();

    String getMimeType();

    String getSubType();

    String getTransferEncoding();
}
