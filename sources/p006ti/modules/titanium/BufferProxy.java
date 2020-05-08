package p006ti.modules.titanium;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.codec.CodecModule;

/* renamed from: ti.modules.titanium.BufferProxy */
public class BufferProxy extends KrollProxy {
    private static final String TAG = "BufferProxy";
    private byte[] buffer;

    public BufferProxy() {
        this(0);
    }

    public BufferProxy(int bufferSize) {
        this.buffer = new byte[bufferSize];
    }

    public BufferProxy(byte[] existingBuffer) {
        this.buffer = existingBuffer;
    }

    public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
        if (args.length == 0) {
            this.buffer = new byte[0];
        } else {
            super.handleCreationArgs(createdInModule, args);
        }
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        int length = 0;
        Object lengthProperty = dict.get(TiC.PROPERTY_LENGTH);
        if (lengthProperty != null) {
            length = TiConvert.toInt(lengthProperty);
        }
        if (!hasProperty(TiC.PROPERTY_BYTE_ORDER)) {
            setProperty(TiC.PROPERTY_BYTE_ORDER, Integer.valueOf(CodecModule.getByteOrder(null)));
        }
        this.buffer = new byte[length];
        Object value = dict.get(TiC.PROPERTY_VALUE);
        if (value instanceof Number) {
            encodeNumber((Number) value, dict);
        } else if (value instanceof String) {
            encodeString((String) value, dict);
        }
    }

    /* access modifiers changed from: protected */
    public void encodeNumber(Number value, KrollDict dict) {
        String type = TiConvert.toString((HashMap<String, Object>) dict, "type");
        if (type == null) {
            throw new IllegalArgumentException("data is a Number, but no type was given");
        }
        if (this.buffer.length == 0) {
            this.buffer = new byte[CodecModule.getWidth(type)];
        }
        CodecModule.encodeNumber(value, type, this.buffer, 0, CodecModule.getByteOrder(dict.get(TiC.PROPERTY_BYTE_ORDER)));
    }

    /* access modifiers changed from: protected */
    public void encodeString(String value, KrollDict dict) {
        String type = TiConvert.toString((HashMap<String, Object>) dict, "type");
        if (type == null) {
            type = CodecModule.CHARSET_UTF8;
        }
        String charset = CodecModule.getCharset(type);
        try {
            byte[] bytes = value.getBytes(charset);
            if (this.buffer.length == 0) {
                this.buffer = bytes;
            } else {
                System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
            }
        } catch (UnsupportedEncodingException e) {
            Log.m46w(TAG, e.getMessage(), (Throwable) e);
            throw new IllegalArgumentException("Unsupported Encoding: " + charset);
        }
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public Object getIndexedProperty(int index) {
        return Integer.valueOf(this.buffer[index] & 255);
    }

    public void setIndexedProperty(int index, Object value) {
        if (value instanceof Number) {
            this.buffer[index] = ((Number) value).byteValue();
        } else {
            super.setIndexedProperty(index, value);
        }
    }

    /* access modifiers changed from: protected */
    public byte[] copyOf(byte[] array, int newLength) {
        byte[] newArray = new byte[newLength];
        int length = newLength;
        if (length > array.length) {
            length = array.length;
        }
        System.arraycopy(array, 0, newArray, 0, length);
        return newArray;
    }

    /* access modifiers changed from: protected */
    public byte[] copyOfRange(byte[] array, int from, int to) {
        int length = to - from;
        byte[] newArray = new byte[length];
        System.arraycopy(array, from, newArray, 0, length);
        return newArray;
    }

    /* access modifiers changed from: protected */
    public void validateOffsetAndLength(int offset, int length, int bufferLength) {
        if (length > offset + bufferLength) {
            throw new IllegalArgumentException("offset of " + offset + " and length of " + length + " is larger than the buffer length: " + bufferLength);
        }
    }

    public int write(int position, byte[] sourceBuffer, int sourceOffset, int sourceLength) {
        if (position + sourceLength > this.buffer.length) {
            this.buffer = copyOf(this.buffer, position + sourceLength);
        }
        System.arraycopy(sourceBuffer, sourceOffset, this.buffer, position, sourceLength);
        return sourceLength;
    }

    public int append(Object[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("At least 1 argument required for append: src");
        }
        int destLength = this.buffer.length;
        byte[] sourceBuffer = args[0].getBuffer();
        int offset = 0;
        if (args.length > 1 && args[1] != null) {
            offset = TiConvert.toInt(args[1]);
        }
        int sourceLength = sourceBuffer.length;
        if (args.length > 2 && args[2] != null) {
            sourceLength = TiConvert.toInt(args[2]);
        }
        validateOffsetAndLength(offset, sourceLength, sourceBuffer.length);
        this.buffer = copyOf(this.buffer, destLength + sourceLength);
        System.arraycopy(sourceBuffer, offset, this.buffer, destLength, sourceLength);
        return sourceLength;
    }

    public int insert(Object[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("At least 2 arguments required for insert: src, offset");
        }
        byte[] sourceBuffer = args[0].getBuffer();
        int offset = TiConvert.toInt(args[1]);
        int sourceOffset = 0;
        if (args.length > 2 && args[2] != null) {
            sourceOffset = TiConvert.toInt(args[2]);
        }
        int sourceLength = sourceBuffer.length;
        if (args.length > 3 && args[3] != null) {
            sourceLength = TiConvert.toInt(args[3]);
        }
        validateOffsetAndLength(sourceOffset, sourceLength, sourceBuffer.length);
        byte[] preInsertBuffer = copyOf(this.buffer, offset);
        byte[] postInsertBuffer = copyOfRange(this.buffer, offset, this.buffer.length);
        this.buffer = new byte[(preInsertBuffer.length + sourceLength + postInsertBuffer.length)];
        System.arraycopy(preInsertBuffer, 0, this.buffer, 0, preInsertBuffer.length);
        System.arraycopy(sourceBuffer, sourceOffset, this.buffer, preInsertBuffer.length, sourceLength);
        System.arraycopy(postInsertBuffer, 0, this.buffer, preInsertBuffer.length + sourceLength, postInsertBuffer.length);
        return sourceLength;
    }

    public int copy(Object[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("At least 1 argument required for copy: srcBuffer");
        }
        byte[] sourceBuffer = args[0].getBuffer();
        int offset = 0;
        if (args.length > 1 && args[1] != null) {
            offset = TiConvert.toInt(args[1]);
        }
        int sourceOffset = 0;
        if (args.length > 2 && args[2] != null) {
            sourceOffset = TiConvert.toInt(args[2]);
        }
        int sourceLength = sourceBuffer.length;
        if (args.length > 3 && args[3] != null) {
            sourceLength = TiConvert.toInt(args[3]);
        }
        validateOffsetAndLength(sourceOffset, sourceLength, sourceBuffer.length);
        System.arraycopy(sourceBuffer, sourceOffset, this.buffer, offset, sourceLength);
        return sourceLength;
    }

    public BufferProxy clone(Object[] args) {
        int offset = 0;
        if (args.length > 0 && args[0] != null) {
            offset = TiConvert.toInt(args[0]);
        }
        int length = this.buffer.length;
        if (args.length > 1 && args[1] != null) {
            length = TiConvert.toInt(args[1]);
        }
        validateOffsetAndLength(offset, length, this.buffer.length);
        return new BufferProxy(copyOfRange(this.buffer, offset, offset + length));
    }

    public void fill(Object[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("fill requires at least 1 argument: fillByte");
        }
        int fillByte = TiConvert.toInt(args[0]);
        int offset = 0;
        if (args.length > 1 && args[1] != null) {
            offset = TiConvert.toInt(args[1]);
        }
        int length = this.buffer.length;
        if (args.length > 2 && args[2] != null) {
            length = TiConvert.toInt(args[2]);
        }
        validateOffsetAndLength(offset, length, this.buffer.length);
        Arrays.fill(this.buffer, offset, offset + length, (byte) fillByte);
    }

    public void clear() {
        Arrays.fill(this.buffer, 0);
    }

    public void release() {
        this.buffer = new byte[0];
    }

    public String toString() {
        return new String(this.buffer);
    }

    public TiBlob toBlob() {
        return TiBlob.blobFromData(this.buffer);
    }

    public int getLength() {
        return this.buffer.length;
    }

    public void setLength(int length) {
        resize(length);
    }

    public void resize(int length) {
        this.buffer = copyOf(this.buffer, length);
    }

    public String getApiName() {
        return "Ti.Buffer";
    }
}
