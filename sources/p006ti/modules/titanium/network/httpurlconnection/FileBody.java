package p006ti.modules.titanium.network.httpurlconnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* renamed from: ti.modules.titanium.network.httpurlconnection.FileBody */
public class FileBody extends AbstractContentBody {
    private final File file;

    public FileBody(File file2, String mimeType) {
        super(mimeType);
        if (file2 == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file2;
    }

    public FileBody(File file2) {
        this(file2, "application/octet-stream");
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    public void writeTo(OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new FileInputStream(this.file);
        try {
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
        } finally {
            in.close();
        }
    }

    public String getTransferEncoding() {
        return "binary";
    }

    public String getCharset() {
        return null;
    }

    public long getContentLength() {
        return this.file.length();
    }

    public String getFilename() {
        return this.file.getName();
    }

    public File getFile() {
        return this.file;
    }
}
