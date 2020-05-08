package p006ti.modules.titanium.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.p005io.TiStream;
import org.appcelerator.titanium.util.TiStreamHelper;
import p006ti.modules.titanium.BufferProxy;

/* renamed from: ti.modules.titanium.stream.BufferStreamProxy */
public class BufferStreamProxy extends KrollProxy implements TiStream {
    private static final String TAG = "BufferStream";
    private BufferProxy buffer;
    private boolean isOpen = false;
    private int mode = -1;
    private int position = -1;

    public BufferStreamProxy(BufferProxy buffer2, int mode2) {
        if (mode2 == 0) {
            this.position = 0;
        } else if (mode2 == 1) {
            this.position = 0;
        } else if (mode2 == 2) {
            this.position = buffer2.getLength();
        } else {
            throw new IllegalArgumentException("invalid mode");
        }
        this.buffer = buffer2;
        this.mode = mode2;
        this.isOpen = true;
    }

    public int read(Object[] args) throws IOException {
        if (!this.isOpen) {
            throw new IOException("Unable to read from buffer, not open");
        } else if (this.mode != 0) {
            throw new IOException("Unable to read on a stream, not opened in read mode");
        } else {
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
                    int bytesRead = TiStreamHelper.read(new ByteArrayInputStream(this.buffer.getBuffer(), this.position, this.buffer.getLength() - this.position), bufferProxy, offset, length);
                    if (bytesRead > -1) {
                        this.position += bytesRead;
                    }
                    return bytesRead;
                } catch (IOException e) {
                    Log.m34e(TAG, "Unable to read from buffer stream, IO error", (Throwable) e);
                    throw new IOException("Unable to read from buffer stream, IO error");
                }
            } else {
                throw new IllegalArgumentException("Invalid number of arguments");
            }
        }
    }

    public int write(Object[] args) throws IOException {
        if (!this.isOpen) {
            throw new IOException("Unable to write to buffer, not open");
        } else if (this.mode == 1 || this.mode == 2) {
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
                int bytesWritten = this.buffer.write(this.position, bufferProxy.getBuffer(), offset, length);
                this.position += bytesWritten;
                return bytesWritten;
            }
            throw new IllegalArgumentException("Invalid number of arguments");
        } else {
            throw new IOException("Unable to write on stream, not opened in read or append mode");
        }
    }

    public boolean isWritable() {
        if (this.mode == 1 || this.mode == 2) {
            return true;
        }
        return false;
    }

    public boolean isReadable() {
        if (this.mode != 0) {
            return false;
        }
        return true;
    }

    public void close() throws IOException {
        this.buffer = null;
        this.mode = -1;
        this.position = -1;
        this.isOpen = false;
    }

    public String getApiName() {
        return "Ti.BufferStream";
    }
}
