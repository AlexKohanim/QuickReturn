package p006ti.modules.titanium.stream;

import java.io.IOException;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiFileProxy;
import org.appcelerator.titanium.p005io.TiStream;
import org.appcelerator.titanium.util.TiStreamHelper;
import p006ti.modules.titanium.BufferProxy;

/* renamed from: ti.modules.titanium.stream.FileStreamProxy */
public class FileStreamProxy extends KrollProxy implements TiStream {
    private static final String TAG = "FileStream";
    private TiFileProxy fileProxy;
    private boolean isOpen = false;

    public FileStreamProxy(TiFileProxy fileProxy2) {
        this.fileProxy = fileProxy2;
        this.isOpen = true;
    }

    public int read(Object[] args) throws IOException {
        if (!this.isOpen) {
            throw new IOException("Unable to read from file, not open");
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
            try {
                return TiStreamHelper.read(this.fileProxy.getBaseFile().getExistingInputStream(), bufferProxy, offset, length);
            } catch (IOException e) {
                Log.m34e(TAG, "Unable to read from file, IO error", (Throwable) e);
                throw new IOException("Unable to read from file, IO error");
            }
        } else {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
    }

    public int write(Object[] args) throws IOException {
        if (!this.isOpen) {
            throw new IOException("Unable to write to file, not open");
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
            try {
                return TiStreamHelper.write(this.fileProxy.getBaseFile().getExistingOutputStream(), bufferProxy, offset, length);
            } catch (IOException e) {
                Log.m34e(TAG, "Unable to write to file, IO error", (Throwable) e);
                throw new IOException("Unable to write to file, IO error");
            }
        } else {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
    }

    public boolean isWritable() {
        return this.fileProxy.getBaseFile().isOpen() && this.fileProxy.getBaseFile().isWriteable();
    }

    public boolean isReadable() {
        return this.fileProxy.getBaseFile().isOpen();
    }

    public void close() throws IOException {
        this.fileProxy.getBaseFile().close();
        this.isOpen = false;
    }

    public String getApiName() {
        return "Ti.Filesystem.FileStream";
    }
}
