package org.appcelerator.titanium.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import p006ti.modules.titanium.BufferProxy;

public class TiStreamHelper {
    public static int read(InputStream inputStream, BufferProxy bufferProxy, int offset, int length) throws IOException {
        byte[] buffer = bufferProxy.getBuffer();
        if (offset + length > buffer.length) {
            length = buffer.length - offset;
        }
        return inputStream.read(buffer, offset, length);
    }

    public static int write(OutputStream outputStream, BufferProxy bufferProxy, int offset, int length) throws IOException {
        byte[] buffer = bufferProxy.getBuffer();
        if (offset + length > buffer.length) {
            length = buffer.length - offset;
        }
        outputStream.write(buffer, offset, length);
        outputStream.flush();
        return length;
    }
}
