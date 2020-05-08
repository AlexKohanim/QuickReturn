package org.appcelerator.kroll.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.appcelerator.kroll.common.Log;

public class KrollStreamHelper {
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final String TAG = "KrollStreamHelper";

    public static void pump(InputStream in, OutputStream out) {
        pump(in, out, 1024);
    }

    public static void pump(InputStream in, OutputStream out, int bufferSize) {
        byte[] buffer = new byte[bufferSize];
        while (true) {
            try {
                int count = in.read(buffer);
                if (count == -1) {
                    return;
                }
                if (out != null) {
                    out.write(buffer, 0, count);
                }
            } catch (IOException e) {
                Log.m34e(TAG, "IOException pumping streams", (Throwable) e);
                return;
            }
        }
    }

    public static void pumpCount(InputStream in, OutputStream out, int byteCount) {
        pumpCount(in, out, byteCount, 1024);
    }

    public static void pumpCount(InputStream in, OutputStream out, int byteCount, int bufferSize) {
        byte[] buffer = new byte[bufferSize];
        int totalCount = 0;
        while (totalCount < byteCount) {
            try {
                int count = in.read(buffer, 0, Math.min(bufferSize, byteCount - totalCount));
                if (count != -1) {
                    totalCount += count;
                    if (out != null) {
                        out.write(buffer, 0, count);
                    }
                } else {
                    return;
                }
            } catch (IOException e) {
                Log.m34e(TAG, "IOException pumping streams", (Throwable) e);
                return;
            }
        }
    }

    public static byte[] toByteArray(InputStream in) {
        return toByteArray(in, 32);
    }

    public static byte[] toByteArray(InputStream in, int size) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        pump(in, out);
        return out.toByteArray();
    }

    public static String toString(InputStream in) {
        if (in == null) {
            return null;
        }
        return new String(toByteArray(in));
    }
}
