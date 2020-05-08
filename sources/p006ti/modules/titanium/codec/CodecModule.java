package p006ti.modules.titanium.codec;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.BufferProxy;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

/* renamed from: ti.modules.titanium.codec.CodecModule */
public class CodecModule extends KrollModule {
    public static final int BIG_ENDIAN = 0;
    public static final String CHARSET_ASCII = "ascii";
    public static final String CHARSET_ISO_LATIN_1 = "latin1";
    public static final String CHARSET_UTF16 = "utf16";
    public static final String CHARSET_UTF16BE = "utf16be";
    public static final String CHARSET_UTF16LE = "utf16le";
    public static final String CHARSET_UTF8 = "utf8";
    public static final int LITTLE_ENDIAN = 1;
    private static final String TAG = "TiCodec";
    public static final String TYPE_BYTE = "byte";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_FLOAT = "float";
    public static final String TYPE_INT = "int";
    public static final String TYPE_LONG = "long";
    public static final String TYPE_SHORT = "short";

    public int encodeNumber(KrollDict args) {
        if (!args.containsKey(TiC.PROPERTY_DEST)) {
            throw new IllegalArgumentException("dest was not specified for encodeNumber");
        } else if (!args.containsKey("source")) {
            throw new IllegalArgumentException("src was not specified for encodeNumber");
        } else if (!args.containsKey("type")) {
            throw new IllegalArgumentException("type was not specified for encodeNumber");
        } else {
            BufferProxy dest = (BufferProxy) args.get(TiC.PROPERTY_DEST);
            Number src = (Number) args.get("source");
            String type = TiConvert.toString((HashMap<String, Object>) args, "type");
            int byteOrder = getByteOrder(args.get(TiC.PROPERTY_BYTE_ORDER));
            int position = 0;
            if (args.containsKey(TiC.PROPERTY_POSITION)) {
                position = TiConvert.toInt((HashMap<String, Object>) args, TiC.PROPERTY_POSITION);
            }
            return encodeNumber(src, type, dest.getBuffer(), position, byteOrder);
        }
    }

    public static int encodeNumber(Number src, String type, byte[] dest, int position, int byteOrder) {
        long l = src.longValue();
        if (type.equals(TYPE_BYTE)) {
            dest[position] = (byte) ((int) (255 & l));
            return position + 1;
        } else if (type.equals(TYPE_SHORT)) {
            int bits = byteOrder == 0 ? 8 : 0;
            int step = byteOrder == 0 ? -8 : 8;
            int i = position;
            while (i < position + 2) {
                dest[i] = (byte) ((int) ((l >>> bits) & 255));
                i++;
                bits += step;
            }
            return position + 2;
        } else if (type.equals(TYPE_INT) || type.equals(TYPE_FLOAT)) {
            if (type.equals(TYPE_FLOAT)) {
                l = (long) Float.floatToIntBits(src.floatValue());
            }
            int bits2 = byteOrder == 0 ? 24 : 0;
            int step2 = byteOrder == 0 ? -8 : 8;
            int j = position;
            while (j < position + 4) {
                dest[j] = (byte) ((int) ((l >>> bits2) & 255));
                j++;
                bits2 += step2;
            }
            return position + 4;
        } else if (!type.equals(TYPE_LONG) && !type.equals(TYPE_DOUBLE)) {
            return position;
        } else {
            if (type.equals(TYPE_DOUBLE)) {
                l = Double.doubleToLongBits(src.doubleValue());
            }
            int bits3 = byteOrder == 0 ? 56 : 0;
            int step3 = byteOrder == 0 ? -8 : 8;
            int i2 = position;
            while (i2 < position + 8) {
                dest[i2] = (byte) ((int) ((l >>> bits3) & 255));
                i2++;
                bits3 += step3;
            }
            return position + 8;
        }
    }

    public Object decodeNumber(KrollDict args) {
        if (!args.containsKey("source")) {
            throw new IllegalArgumentException("src was not specified for encodeNumber");
        } else if (!args.containsKey("type")) {
            throw new IllegalArgumentException("type was not specified for encodeNumber");
        } else {
            BufferProxy buffer = (BufferProxy) args.get("source");
            String type = (String) args.get("type");
            int byteOrder = getByteOrder(args.get(TiC.PROPERTY_BYTE_ORDER));
            int position = 0;
            if (args.containsKey(TiC.PROPERTY_POSITION)) {
                position = TiConvert.toInt((HashMap<String, Object>) args, TiC.PROPERTY_POSITION);
            }
            byte[] src = buffer.getBuffer();
            if (type.equals(TYPE_BYTE)) {
                return Byte.valueOf(src[position]);
            }
            if (type.equals(TYPE_SHORT)) {
                short s1 = (short) (src[position] & 255);
                short s2 = (short) (src[position + 1] & 255);
                switch (byteOrder) {
                    case 0:
                        return Integer.valueOf((s1 << 8) + s2);
                    case 1:
                        return Integer.valueOf((s2 << 8) + s1);
                }
            } else if (type.equals(TYPE_INT) || type.equals(TYPE_FLOAT)) {
                int bits = 0;
                int shiftBits = byteOrder == 0 ? 24 : 0;
                int step = byteOrder == 0 ? -8 : 8;
                int i = 0;
                while (i < 4) {
                    bits += (src[position + i] & 255) << shiftBits;
                    i++;
                    shiftBits += step;
                }
                if (type.equals(TYPE_FLOAT)) {
                    return Float.valueOf(Float.intBitsToFloat(bits));
                }
                return Integer.valueOf(bits);
            } else if (type.equals(TYPE_LONG) || type.equals(TYPE_DOUBLE)) {
                long bits2 = 0;
                int shiftBits2 = byteOrder == 0 ? 56 : 0;
                int step2 = byteOrder == 0 ? -8 : 8;
                int i2 = 0;
                while (i2 < 8) {
                    bits2 += ((long) (src[position + i2] & 255)) << shiftBits2;
                    i2++;
                    shiftBits2 += step2;
                }
                if (type.equals(TYPE_DOUBLE)) {
                    return Double.valueOf(Double.longBitsToDouble(bits2));
                }
                return Long.valueOf(bits2);
            }
            return Integer.valueOf(0);
        }
    }

    public int encodeString(KrollDict args) {
        if (!args.containsKey(TiC.PROPERTY_DEST)) {
            throw new IllegalArgumentException("dest was not specified for encodeString");
        } else if (!args.containsKey("source") || args.get("source") == null) {
            throw new IllegalArgumentException("src was not specified for encodeString");
        } else {
            BufferProxy dest = (BufferProxy) args.get(TiC.PROPERTY_DEST);
            String src = (String) args.get("source");
            int destPosition = 0;
            if (args.containsKey(TiC.PROPERTY_DEST_POSITION)) {
                destPosition = TiConvert.toInt((HashMap<String, Object>) args, TiC.PROPERTY_DEST_POSITION);
            }
            int srcPosition = 0;
            if (args.containsKey(TiC.PROPERTY_SOURCE_POSITION)) {
                srcPosition = TiConvert.toInt((HashMap<String, Object>) args, TiC.PROPERTY_SOURCE_POSITION);
            }
            int srcLength = src.length();
            if (args.containsKey(TiC.PROPERTY_SOURCE_LENGTH)) {
                srcLength = TiConvert.toInt((HashMap<String, Object>) args, TiC.PROPERTY_SOURCE_LENGTH);
            }
            String charset = validateCharset(args);
            byte[] destBuffer = dest.getBuffer();
            validatePositionAndLength(srcPosition, srcLength, src.length());
            if (!(srcPosition == 0 && srcLength == src.length())) {
                src = src.substring(srcPosition, srcPosition + srcLength);
            }
            try {
                byte[] encoded = src.getBytes(charset);
                System.arraycopy(encoded, 0, destBuffer, destPosition, encoded.length);
                return encoded.length + destPosition;
            } catch (UnsupportedEncodingException e) {
                Log.m46w(TAG, e.getMessage(), (Throwable) e);
                throw new IllegalArgumentException("Unsupported Encoding: " + charset);
            }
        }
    }

    public String decodeString(KrollDict args) {
        if (!args.containsKey("source") || args.get("source") == null) {
            throw new IllegalArgumentException("src was not specified for decodeString");
        }
        byte[] buffer = ((BufferProxy) args.get("source")).getBuffer();
        int position = 0;
        if (args.containsKey(TiC.PROPERTY_POSITION)) {
            position = TiConvert.toInt((HashMap<String, Object>) args, TiC.PROPERTY_POSITION);
        }
        int length = buffer.length;
        if (args.containsKey(TiC.PROPERTY_LENGTH)) {
            length = TiConvert.toInt((HashMap<String, Object>) args, TiC.PROPERTY_LENGTH);
        }
        validatePositionAndLength(position, length, buffer.length);
        String charset = validateCharset(args);
        try {
            return new String(buffer, position, length, charset);
        } catch (UnsupportedEncodingException e) {
            Log.m46w(TAG, e.getMessage(), (Throwable) e);
            throw new IllegalArgumentException("Unsupported Encoding: " + charset);
        }
    }

    public int getNativeByteOrder() {
        return getByteOrder(null);
    }

    public static int getWidth(String dataType) {
        if (TYPE_BYTE.equals(dataType)) {
            return 1;
        }
        if (TYPE_SHORT.equals(dataType)) {
            return 2;
        }
        if (TYPE_INT.equals(dataType) || TYPE_FLOAT.equals(dataType)) {
            return 4;
        }
        if (TYPE_LONG.equals(dataType) || TYPE_DOUBLE.equals(dataType)) {
            return 8;
        }
        return 0;
    }

    public static int getByteOrder(Object byteOrder) {
        if (byteOrder instanceof Number) {
            return ((Number) byteOrder).intValue();
        }
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            return 0;
        }
        return 1;
    }

    public static String getCharset(String charset) {
        if (CHARSET_ASCII.equals(charset)) {
            return "US-ASCII";
        }
        if (CHARSET_ISO_LATIN_1.equals(charset)) {
            return "ISO-8859-1";
        }
        if (CHARSET_UTF8.equals(charset)) {
            return HttpUrlConnectionUtils.UTF_8;
        }
        if (CHARSET_UTF16.equals(charset)) {
            return "UTF-16";
        }
        if (CHARSET_UTF16LE.equals(charset)) {
            return "UTF-16LE";
        }
        if (CHARSET_UTF16BE.equals(charset)) {
            return "UTF-16BE";
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public String validateCharset(KrollDict args) {
        String charset = HttpUrlConnectionUtils.UTF_8;
        if (args.containsKey(TiC.PROPERTY_CHARSET)) {
            charset = getCharset(TiConvert.toString((HashMap<String, Object>) args, TiC.PROPERTY_CHARSET));
        }
        if (charset != null) {
            return charset;
        }
        throw new IllegalArgumentException("could not find a valid charset for " + args.get(TiC.PROPERTY_CHARSET));
    }

    /* access modifiers changed from: protected */
    public void validatePositionAndLength(int position, int length, int expectedLength) {
        if (position + length > expectedLength) {
            throw new IllegalArgumentException("position " + position + " and length " + length + " is bigger than the expected length: " + expectedLength);
        }
    }

    public String getApiName() {
        return "Ti.Codec";
    }
}
