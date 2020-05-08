package org.appcelerator.titanium.p005io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;

/* renamed from: org.appcelerator.titanium.io.TiBaseFile */
public abstract class TiBaseFile {
    public static final int MODE_APPEND = 2;
    public static final int MODE_READ = 0;
    public static final int MODE_WRITE = 1;
    private static final String TAG = "TiBaseFile";
    protected static final int TYPE_BLOB = 3;
    protected static final int TYPE_FILE = 1;
    protected static final int TYPE_RESOURCE = 2;
    protected boolean binary = false;
    protected boolean flagHidden = false;
    protected boolean flagSymbolicLink = false;
    protected BufferedReader inreader = null;
    protected InputStream instream = null;
    protected boolean modeExecutable = false;
    protected boolean modeRead = true;
    protected boolean modeWrite = false;
    protected boolean opened = false;
    protected OutputStream outstream = null;
    protected BufferedWriter outwriter = null;
    protected boolean stream = false;
    protected int type;
    protected boolean typeDir = false;
    protected boolean typeFile = true;

    public abstract InputStream getInputStream() throws IOException;

    public abstract File getNativeFile();

    public abstract OutputStream getOutputStream() throws IOException;

    protected TiBaseFile(int type2) {
        this.type = type2;
    }

    public boolean isFile() {
        return this.typeFile;
    }

    public boolean isDirectory() {
        return this.typeDir;
    }

    public boolean isExecutable() {
        return this.modeExecutable;
    }

    public boolean isReadonly() {
        return this.modeRead && !this.modeWrite;
    }

    public boolean isWriteable() {
        return this.modeWrite;
    }

    public boolean isHidden() {
        return this.flagHidden;
    }

    public boolean isSymbolicLink() {
        return this.flagSymbolicLink;
    }

    /* JADX WARNING: Removed duplicated region for block: B:57:0x0074 A[SYNTHETIC, Splitter:B:57:0x0074] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x007a A[SYNTHETIC, Splitter:B:61:0x007a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean copy(java.lang.String r14) throws java.io.IOException {
        /*
            r13 = this;
            r11 = 0
            r6 = 0
            r8 = 0
            r2 = 0
            if (r14 != 0) goto L_0x0008
            r3 = r2
        L_0x0007:
            return r11
        L_0x0008:
            java.io.InputStream r6 = r13.getInputStream()     // Catch:{ IOException -> 0x00ae }
            if (r6 != 0) goto L_0x001c
            if (r6 == 0) goto L_0x0014
            r6.close()     // Catch:{ IOException -> 0x0096 }
            r6 = 0
        L_0x0014:
            if (r8 == 0) goto L_0x001a
            r8.close()     // Catch:{ IOException -> 0x0099 }
            r8 = 0
        L_0x001a:
            r3 = r2
            goto L_0x0007
        L_0x001c:
            r12 = 1
            java.lang.String[] r10 = new java.lang.String[r12]     // Catch:{ IOException -> 0x00ae }
            r12 = 0
            r10[r12] = r14     // Catch:{ IOException -> 0x00ae }
            r12 = 0
            org.appcelerator.titanium.io.TiBaseFile r0 = org.appcelerator.titanium.p005io.TiFileFactory.createTitaniumFile(r10, r12)     // Catch:{ IOException -> 0x00ae }
            if (r0 != 0) goto L_0x0037
            if (r6 == 0) goto L_0x002f
            r6.close()     // Catch:{ IOException -> 0x009b }
            r6 = 0
        L_0x002f:
            if (r8 == 0) goto L_0x0035
            r8.close()     // Catch:{ IOException -> 0x009d }
            r8 = 0
        L_0x0035:
            r3 = r2
            goto L_0x0007
        L_0x0037:
            java.io.OutputStream r8 = r0.getOutputStream()     // Catch:{ IOException -> 0x00ae }
            if (r8 != 0) goto L_0x004b
            if (r6 == 0) goto L_0x0043
            r6.close()     // Catch:{ IOException -> 0x009f }
            r6 = 0
        L_0x0043:
            if (r8 == 0) goto L_0x0049
            r8.close()     // Catch:{ IOException -> 0x00a1 }
            r8 = 0
        L_0x0049:
            r3 = r2
            goto L_0x0007
        L_0x004b:
            r11 = 8096(0x1fa0, float:1.1345E-41)
            byte[] r1 = new byte[r11]     // Catch:{ IOException -> 0x00ae }
            r4 = 0
            java.io.BufferedInputStream r7 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x00ae }
            r7.<init>(r6)     // Catch:{ IOException -> 0x00ae }
            java.io.BufferedOutputStream r9 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x00b0, all -> 0x00a7 }
            r9.<init>(r8)     // Catch:{ IOException -> 0x00b0, all -> 0x00a7 }
        L_0x005a:
            int r4 = r7.read(r1)     // Catch:{ IOException -> 0x0066, all -> 0x00aa }
            r11 = -1
            if (r4 == r11) goto L_0x007f
            r11 = 0
            r9.write(r1, r11, r4)     // Catch:{ IOException -> 0x0066, all -> 0x00aa }
            goto L_0x005a
        L_0x0066:
            r5 = move-exception
            r8 = r9
            r6 = r7
        L_0x0069:
            java.lang.String r11 = "TiBaseFile"
            java.lang.String r12 = "Error while copying file: "
            org.appcelerator.kroll.common.Log.m34e(r11, r12, r5)     // Catch:{ all -> 0x0071 }
            throw r5     // Catch:{ all -> 0x0071 }
        L_0x0071:
            r11 = move-exception
        L_0x0072:
            if (r6 == 0) goto L_0x0078
            r6.close()     // Catch:{ IOException -> 0x00a3 }
            r6 = 0
        L_0x0078:
            if (r8 == 0) goto L_0x007e
            r8.close()     // Catch:{ IOException -> 0x00a5 }
            r8 = 0
        L_0x007e:
            throw r11
        L_0x007f:
            r2 = 1
            if (r7 == 0) goto L_0x00b5
            r7.close()     // Catch:{ IOException -> 0x0090 }
            r6 = 0
        L_0x0086:
            if (r9 == 0) goto L_0x00b3
            r9.close()     // Catch:{ IOException -> 0x0093 }
            r8 = 0
        L_0x008c:
            r3 = r2
            r11 = r2
            goto L_0x0007
        L_0x0090:
            r11 = move-exception
            r6 = r7
            goto L_0x0086
        L_0x0093:
            r11 = move-exception
            r8 = r9
            goto L_0x008c
        L_0x0096:
            r12 = move-exception
            goto L_0x0014
        L_0x0099:
            r12 = move-exception
            goto L_0x001a
        L_0x009b:
            r12 = move-exception
            goto L_0x002f
        L_0x009d:
            r12 = move-exception
            goto L_0x0035
        L_0x009f:
            r12 = move-exception
            goto L_0x0043
        L_0x00a1:
            r12 = move-exception
            goto L_0x0049
        L_0x00a3:
            r12 = move-exception
            goto L_0x0078
        L_0x00a5:
            r12 = move-exception
            goto L_0x007e
        L_0x00a7:
            r11 = move-exception
            r6 = r7
            goto L_0x0072
        L_0x00aa:
            r11 = move-exception
            r8 = r9
            r6 = r7
            goto L_0x0072
        L_0x00ae:
            r5 = move-exception
            goto L_0x0069
        L_0x00b0:
            r5 = move-exception
            r6 = r7
            goto L_0x0069
        L_0x00b3:
            r8 = r9
            goto L_0x008c
        L_0x00b5:
            r6 = r7
            goto L_0x0086
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.p005io.TiBaseFile.copy(java.lang.String):boolean");
    }

    public boolean createDirectory(boolean recursive) {
        logNotSupported("createDirectory");
        return false;
    }

    public boolean createFile() {
        logNotSupported("createFile");
        return false;
    }

    public boolean createShortcut() {
        logNotSupported("createShortcut");
        return false;
    }

    public double createTimestamp() {
        logNotSupported("createTimestamp");
        return 0.0d;
    }

    public boolean deleteDirectory(boolean recursive) {
        logNotSupported("deleteDirectory");
        return false;
    }

    public boolean deleteFile() {
        logNotSupported("deleteFile");
        return false;
    }

    public boolean exists() {
        logNotSupported("exists");
        return false;
    }

    public String extension() {
        logNotSupported("extensionsion");
        return null;
    }

    public List<String> getDirectoryListing() {
        logNotSupported("getDirectoryListing");
        return null;
    }

    public TiBaseFile getParent() {
        logNotSupported("getParent");
        return null;
    }

    public double modificationTimestamp() {
        logNotSupported("modificationTimestamp");
        return 0.0d;
    }

    public boolean move(String destination) throws IOException {
        if (destination == null) {
            return false;
        }
        TiBaseFile bf = TiFileFactory.createTitaniumFile(new String[]{destination}, false);
        if (bf == null) {
            throw new FileNotFoundException("Destination not found: " + destination);
        } else if (bf.exists()) {
            throw new IOException("Destination already exists.");
        } else if (getNativeFile() == null) {
            throw new FileNotFoundException("Source is not a true file.");
        } else if (bf.getNativeFile() == null) {
            throw new FileNotFoundException("Destination is not a valid location for writing");
        } else if (copy(destination)) {
            return deleteFile();
        } else {
            return false;
        }
    }

    public String name() {
        logNotSupported(TiC.PROPERTY_NAME);
        return null;
    }

    public String nativePath() {
        logNotSupported("nativePath");
        return null;
    }

    public TiBlob read() throws IOException {
        logNotSupported("read");
        return null;
    }

    public String readLine() throws IOException {
        logNotSupported("readLine");
        return null;
    }

    public boolean rename(String destination) {
        if (destination == null) {
            return false;
        }
        File f = getNativeFile();
        if (f != null) {
            return f.renameTo(new File(f.getParent(), destination));
        }
        return false;
    }

    public TiBaseFile resolve() {
        logNotSupported("resolve");
        return null;
    }

    public boolean setExecutable() {
        logNotSupported("setExecutable");
        return false;
    }

    public boolean setReadonly() {
        logNotSupported("setReadonly");
        return false;
    }

    public boolean setWriteable() {
        logNotSupported("setWriteable");
        return false;
    }

    public long size() {
        logNotSupported("size");
        return 0;
    }

    public double spaceAvailable() {
        logNotSupported("spaceAvailable");
        return 0.0d;
    }

    public void unzip(String destination) {
        logNotSupported("unzip");
    }

    public void write(TiBlob blob, boolean append) throws IOException {
    }

    public void write(String data, boolean append) throws IOException {
        logNotSupported("write");
    }

    public void writeFromUrl(String url, boolean append) throws IOException {
        logNotSupported("writeFromUrl");
    }

    public void writeLine(String data) throws IOException {
        logNotSupported("writeLine");
    }

    public void close() throws IOException {
        if (this.opened) {
            if (this.instream != null) {
                try {
                    this.instream.close();
                    this.instream = null;
                } catch (IOException e) {
                    throw new IOException("Error closing file");
                }
            }
            if (this.inreader != null) {
                try {
                    this.inreader.close();
                    this.inreader = null;
                } catch (IOException e2) {
                    throw new IOException("Error closing file");
                }
            }
            if (this.outstream != null) {
                try {
                    this.outstream.close();
                    this.outstream = null;
                } catch (IOException e3) {
                    throw new IOException("Error closing file");
                }
            }
            if (this.outwriter != null) {
                try {
                    this.outwriter.close();
                    this.outwriter = null;
                } catch (IOException e4) {
                    throw new IOException("Error closing file");
                }
            }
            this.opened = false;
        }
        this.binary = false;
    }

    public boolean isOpen() {
        return this.opened;
    }

    public void open(int mode, boolean binary2) throws IOException {
        logNotSupported(TiC.EVENT_OPEN);
    }

    /* access modifiers changed from: protected */
    public void logNotSupported(String method) {
        if (method == null) {
            method = Thread.currentThread().getStackTrace()[1].getMethodName();
        }
        Log.m44w(TAG, "Method is not supported " + getClass().getName() + " : " + method);
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[8096];
        while (true) {
            int count = is.read(buf);
            if (count != -1) {
                os.write(buf, 0, count);
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void copyStream(Reader r, Writer w) throws IOException {
        char[] buf = new char[8096];
        int count = 0;
        while (true) {
            count = r.read(buf, 0, count);
            if (count != -1) {
                w.write(buf, 0, count);
            } else {
                return;
            }
        }
    }

    public InputStream getExistingInputStream() {
        return this.instream;
    }

    public OutputStream getExistingOutputStream() {
        return this.outstream;
    }
}
