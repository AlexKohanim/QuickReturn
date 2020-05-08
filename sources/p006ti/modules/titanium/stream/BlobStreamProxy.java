package p006ti.modules.titanium.stream;

import java.io.IOException;
import java.io.InputStream;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.p005io.TiStream;
import org.appcelerator.titanium.util.TiStreamHelper;
import p006ti.modules.titanium.BufferProxy;

/* renamed from: ti.modules.titanium.stream.BlobStreamProxy */
public class BlobStreamProxy extends KrollProxy implements TiStream {
    private InputStream inputStream = null;
    private boolean isOpen = false;
    private TiBlob tiBlob;

    public BlobStreamProxy(TiBlob tiBlob2) {
        this.tiBlob = tiBlob2;
        this.isOpen = true;
    }

    public int read(Object[] args) throws IOException {
        if (!this.isOpen) {
            throw new IOException("Unable to read from blob, not open");
        }
        BufferProxy bufferProxy = null;
        int offset = 0;
        int length = 0;
        if (args.length == 1 || args.length == 3) {
            if (args.length > 0) {
                if (args[0] instanceof BufferProxy) {
                    bufferProxy = args[0];
                    length = bufferProxy.getLength();
                } else {
                    throw new IllegalArgumentException("Invalid buffer argument");
                }
            }
            if (args.length == 3) {
                if (args[1] instanceof Integer) {
                    offset = args[1].intValue();
                } else if (args[1] instanceof Double) {
                    offset = args[1].intValue();
                } else {
                    throw new IllegalArgumentException("Invalid offset argument");
                }
                if (args[2] instanceof Integer) {
                    length = args[2].intValue();
                } else if (args[2] instanceof Double) {
                    length = args[2].intValue();
                } else {
                    throw new IllegalArgumentException("Invalid length argument");
                }
            }
            if (this.inputStream == null) {
                this.inputStream = this.tiBlob.getInputStream();
            }
            if (this.inputStream != null) {
                try {
                    return TiStreamHelper.read(this.inputStream, bufferProxy, offset, length);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IOException("Unable to read from blob, IO error");
                }
            } else {
                throw new IOException("Unable to read from blob, input stream is null");
            }
        } else {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
    }

    public int write(Object[] args) throws IOException {
        throw new IOException("Unable to write, blob is read only");
    }

    public boolean isWritable() {
        return false;
    }

    public boolean isReadable() {
        return true;
    }

    public void close() throws IOException {
        this.tiBlob = null;
        this.inputStream.close();
        this.isOpen = false;
    }

    public String getApiName() {
        return "Ti.BlobStream";
    }
}
