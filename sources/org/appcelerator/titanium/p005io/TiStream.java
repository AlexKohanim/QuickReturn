package org.appcelerator.titanium.p005io;

import java.io.IOException;

/* renamed from: org.appcelerator.titanium.io.TiStream */
public interface TiStream {
    void close() throws IOException;

    boolean isReadable();

    boolean isWritable();

    int read(Object[] objArr) throws IOException;

    int write(Object[] objArr) throws IOException;
}
