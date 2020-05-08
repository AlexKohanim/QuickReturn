package p006ti.modules.titanium.network.httpurlconnection;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.json.JSONObject;

/* renamed from: ti.modules.titanium.network.httpurlconnection.JsonBody */
public class JsonBody extends AbstractContentBody {
    private static final String CONTENT_TYPE = "application/json";
    private byte[] data;
    private String filename;
    private String value;

    public JsonBody(JSONObject jsonObject, String filename2) {
        super(CONTENT_TYPE);
        this.value = jsonObject.toString();
        this.filename = filename2;
        try {
            this.data = this.value.getBytes(HttpUrlConnectionUtils.UTF_8);
        } catch (UnsupportedEncodingException e) {
            this.data = this.value.getBytes();
        }
    }

    public String getFilename() {
        return this.filename;
    }

    public String getCharset() {
        return HttpUrlConnectionUtils.UTF_8;
    }

    public long getContentLength() {
        return (long) this.data.length;
    }

    public String getTransferEncoding() {
        return "8bit";
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.data);
        out.flush();
    }
}
