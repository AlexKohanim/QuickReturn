package p006ti.modules.titanium.utils;

import android.util.Base64;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.DigestUtils;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiFileProxy;
import org.appcelerator.titanium.util.TiMimeTypeHelper;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

/* renamed from: ti.modules.titanium.utils.UtilsModule */
public class UtilsModule extends KrollModule {
    private static final String TAG = "UtilsModule";

    private String convertToString(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof TiBlob) {
            return ((TiBlob) obj).getText();
        }
        throw new IllegalArgumentException("Invalid type for argument");
    }

    public TiBlob base64encode(Object obj) {
        if (obj instanceof TiBlob) {
            return TiBlob.blobFromString(((TiBlob) obj).toBase64());
        }
        if (obj instanceof TiFileProxy) {
            try {
                return TiBlob.blobFromStreamBase64(((TiFileProxy) obj).getInputStream(), TiMimeTypeHelper.getMimeType(((TiFileProxy) obj).getBaseFile().nativePath()));
            } catch (IOException e) {
                Log.m32e(TAG, "Problem reading file");
            }
        }
        String data = convertToString(obj);
        if (data != null) {
            try {
                return TiBlob.blobFromString(new String(Base64.encode(data.getBytes(HttpUrlConnectionUtils.UTF_8), 2), HttpUrlConnectionUtils.UTF_8));
            } catch (UnsupportedEncodingException e2) {
                Log.m32e(TAG, "UTF-8 is not a supported encoding type");
            }
        }
        return null;
    }

    public TiBlob base64decode(Object obj) {
        String data = convertToString(obj);
        if (data != null) {
            try {
                return TiBlob.blobFromData(Base64.decode(data.getBytes(HttpUrlConnectionUtils.UTF_8), 2));
            } catch (UnsupportedEncodingException e) {
                Log.m32e(TAG, "UTF-8 is not a supported encoding type");
            }
        }
        return null;
    }

    public String md5HexDigest(Object obj) {
        if (obj instanceof TiBlob) {
            return DigestUtils.md5Hex(((TiBlob) obj).getBytes());
        }
        String data = convertToString(obj);
        if (data != null) {
            return DigestUtils.md5Hex(data);
        }
        return null;
    }

    public String sha1(Object obj) {
        if (obj instanceof TiBlob) {
            return DigestUtils.shaHex(((TiBlob) obj).getBytes());
        }
        String data = convertToString(obj);
        if (data != null) {
            return DigestUtils.shaHex(data);
        }
        return null;
    }

    public boolean arrayTest(float[] a, long[] b, int[] c, String[] d) {
        return true;
    }

    public String sha256(Object obj) {
        byte[] b;
        try {
            if (obj instanceof TiBlob) {
                b = ((TiBlob) obj).getBytes();
            } else {
                b = convertToString(obj).getBytes();
            }
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            algorithm.reset();
            algorithm.update(b);
            byte[] messageDigest = algorithm.digest();
            StringBuilder result = new StringBuilder();
            for (byte b2 : messageDigest) {
                result.append(Integer.toString((b2 & 255) + 256, 16).substring(1));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.m32e(TAG, "SHA256 is not a supported algorithm");
            return null;
        }
    }

    public String transcodeString(String orig, String inEncoding, String outEncoding) {
        try {
            Charset charsetOut = Charset.forName(outEncoding);
            Charset charsetIn = Charset.forName(inEncoding);
            ByteBuffer bufferIn = ByteBuffer.wrap(orig.getBytes(charsetIn.name()));
            CharBuffer dataIn = charsetIn.decode(bufferIn);
            bufferIn.clear();
            ByteBuffer bufferOut = charsetOut.encode(dataIn);
            dataIn.clear();
            byte[] dataOut = bufferOut.array();
            bufferOut.clear();
            return new String(dataOut, charsetOut.name());
        } catch (UnsupportedEncodingException e) {
            Log.m34e(TAG, "Unsupported encoding: " + e.getMessage(), (Throwable) e);
            return null;
        }
    }

    public String getApiName() {
        return "Ti.Utils";
    }
}
