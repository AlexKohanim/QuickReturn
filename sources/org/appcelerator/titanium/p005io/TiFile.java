package org.appcelerator.titanium.p005io;

import android.net.Uri;
import android.os.StatFs;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.util.TiUrl;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

/* renamed from: org.appcelerator.titanium.io.TiFile */
public class TiFile extends TiBaseFile {
    private static final String TAG = "TiFile";
    private final File file;
    private final String path;

    public TiFile(File file2, String path2, boolean stream) {
        super(1);
        this.file = file2;
        this.path = path2;
        this.stream = stream;
    }

    public boolean isFile() {
        return this.file.isFile();
    }

    public boolean isDirectory() {
        return this.file.isDirectory();
    }

    public boolean isHidden() {
        return this.file.isHidden();
    }

    public boolean isReadonly() {
        return this.file.canRead() && !this.file.canWrite();
    }

    public boolean isWriteable() {
        return this.file.canWrite();
    }

    public boolean createDirectory(boolean recursive) {
        if (recursive) {
            return this.file.mkdirs();
        }
        return this.file.mkdir();
    }

    public boolean createFile() {
        try {
            if (!this.file.getParentFile().exists()) {
                this.file.mkdirs();
            }
            if (!this.file.exists()) {
                return this.file.createNewFile();
            }
        } catch (IOException e) {
            Log.m34e(TAG, "Error creating new file: ", (Throwable) e);
        }
        return false;
    }

    private boolean deleteTree(File d) {
        boolean deleted = true;
        File[] files = d.listFiles();
        if (files == null) {
            return false;
        }
        for (File f : files) {
            if (!f.isFile()) {
                if (!deleteTree(f)) {
                    break;
                }
                deleted = f.delete();
            } else {
                deleted = f.delete();
                if (!deleted) {
                    break;
                }
            }
        }
        boolean z = deleted;
        return deleted;
    }

    public boolean deleteDirectory(boolean recursive) {
        if (!recursive) {
            return this.file.delete();
        }
        boolean deleted = deleteTree(this.file);
        if (deleted) {
            return this.file.delete();
        }
        return deleted;
    }

    public boolean deleteFile() {
        return this.file.delete();
    }

    public boolean exists() {
        return this.file.exists();
    }

    public double createTimestamp() {
        return (double) this.file.lastModified();
    }

    public double modificationTimestamp() {
        return (double) this.file.lastModified();
    }

    public String name() {
        return this.file.getName();
    }

    public String extension() {
        String name = this.file.getName();
        int idx = name.lastIndexOf(TiUrl.CURRENT_PATH);
        if (idx != -1) {
            return name.substring(idx + 1);
        }
        return null;
    }

    public String nativePath() {
        if (this.file != null) {
            return "file://" + this.file.getAbsolutePath();
        }
        return null;
    }

    public String toURL() {
        return Uri.fromFile(this.file).toString();
    }

    public long size() {
        return this.file.length();
    }

    public double spaceAvailable() {
        StatFs stat = new StatFs(this.file.getPath());
        return ((double) stat.getAvailableBlocks()) * ((double) stat.getBlockSize());
    }

    public boolean setReadonly() {
        this.file.setReadOnly();
        return isReadonly();
    }

    public String toString() {
        return this.path;
    }

    public File getFile() {
        return this.file;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    public OutputStream getOutputStream() throws IOException {
        return getOutputStream(1);
    }

    public OutputStream getOutputStream(int mode) throws IOException {
        return new FileOutputStream(this.file, mode == 2);
    }

    public File getNativeFile() {
        return this.file;
    }

    public List<String> getDirectoryListing() {
        File dir = getNativeFile();
        List<String> listing = new ArrayList<>();
        String[] names = dir.list();
        if (names != null) {
            for (String add : names) {
                listing.add(add);
            }
        }
        return listing;
    }

    public TiBaseFile getParent() {
        File f = getNativeFile();
        if (f == null) {
            return null;
        }
        File p = f.getParentFile();
        if (p != null) {
            return TiFileFactory.createTitaniumFile("file://" + p.getAbsolutePath(), false);
        }
        return null;
    }

    public void open(int mode, boolean binary) throws IOException {
        this.binary = binary;
        if (mode != 0) {
            OutputStream os = getOutputStream(mode);
            if (binary) {
                this.outstream = new BufferedOutputStream(os);
            } else {
                this.outwriter = new BufferedWriter(new OutputStreamWriter(os));
            }
        } else if (!this.file.exists()) {
            throw new FileNotFoundException(this.file.getAbsolutePath());
        } else if (binary) {
            this.instream = new BufferedInputStream(getInputStream());
        } else {
            this.inreader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), "utf-8"));
        }
        this.opened = true;
    }

    public TiBlob read() throws IOException {
        return TiBlob.blobFromFile(this);
    }

    public String readLine() throws IOException {
        String result = null;
        if (!this.opened) {
            throw new IOException("Must open before calling readLine");
        } else if (this.binary) {
            throw new IOException("File opened in binary mode, readLine not available.");
        } else {
            try {
                return this.inreader.readLine();
            } catch (IOException e) {
                Log.m34e(TAG, "Error reading a line from the file: ", (Throwable) e);
                return result;
            }
        }
    }

    public void write(TiBlob blob, boolean append) throws IOException {
        int i = 1;
        Log.m29d(TAG, "write called for file = " + this.file, Log.DEBUG_MODE);
        if (blob == null) {
            return;
        }
        if (!this.stream) {
            if (append) {
                i = 2;
            }
            try {
                open(i, true);
                copyStream(blob.getInputStream(), this.outstream);
            } finally {
                close();
            }
        } else if (!this.opened) {
            throw new IOException("Must open before calling write");
        } else if (this.binary) {
            copyStream(blob.getInputStream(), this.outstream);
        } else {
            this.outwriter.write(new String(blob.getBytes(), HttpUrlConnectionUtils.UTF_8));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x0096  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeFromUrl(java.lang.String r10, boolean r11) throws java.io.IOException {
        /*
            r9 = this;
            r5 = 1
            java.lang.String r6 = "TiFile"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "write called for file = "
            java.lang.StringBuilder r7 = r7.append(r8)
            java.io.File r8 = r9.file
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.String r7 = r7.toString()
            java.lang.String r8 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r6, r7, r8)
            java.lang.String[] r4 = new java.lang.String[r5]
            r6 = 0
            r4[r6] = r10
            org.appcelerator.titanium.io.TiBaseFile r0 = org.appcelerator.titanium.p005io.TiFileFactory.createTitaniumFile(r4, r11)
            if (r0 == 0) goto L_0x0045
            boolean r6 = r9.stream
            if (r6 != 0) goto L_0x0050
            r3 = 0
            if (r11 == 0) goto L_0x0030
            r5 = 2
        L_0x0030:
            r6 = 1
            r9.open(r5, r6)     // Catch:{ all -> 0x0046 }
            java.io.InputStream r3 = r0.getInputStream()     // Catch:{ all -> 0x0046 }
            java.io.OutputStream r5 = r9.outstream     // Catch:{ all -> 0x0046 }
            copyStream(r3, r5)     // Catch:{ all -> 0x0046 }
            if (r3 == 0) goto L_0x0042
            r3.close()
        L_0x0042:
            r9.close()
        L_0x0045:
            return
        L_0x0046:
            r5 = move-exception
            if (r3 == 0) goto L_0x004c
            r3.close()
        L_0x004c:
            r9.close()
            throw r5
        L_0x0050:
            boolean r5 = r9.opened
            if (r5 != 0) goto L_0x005c
            java.io.IOException r5 = new java.io.IOException
            java.lang.String r6 = "Must open before calling write"
            r5.<init>(r6)
            throw r5
        L_0x005c:
            boolean r5 = r9.binary
            if (r5 == 0) goto L_0x0077
            r3 = 0
            java.io.InputStream r3 = r0.getInputStream()     // Catch:{ all -> 0x0070 }
            java.io.OutputStream r5 = r9.outstream     // Catch:{ all -> 0x0070 }
            copyStream(r3, r5)     // Catch:{ all -> 0x0070 }
            if (r3 == 0) goto L_0x0045
            r3.close()
            goto L_0x0045
        L_0x0070:
            r5 = move-exception
            if (r3 == 0) goto L_0x0076
            r3.close()
        L_0x0076:
            throw r5
        L_0x0077:
            r1 = 0
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ all -> 0x0093 }
            java.io.InputStreamReader r5 = new java.io.InputStreamReader     // Catch:{ all -> 0x0093 }
            java.io.InputStream r6 = r0.getInputStream()     // Catch:{ all -> 0x0093 }
            java.lang.String r7 = "utf-8"
            r5.<init>(r6, r7)     // Catch:{ all -> 0x0093 }
            r2.<init>(r5)     // Catch:{ all -> 0x0093 }
            java.io.BufferedWriter r5 = r9.outwriter     // Catch:{ all -> 0x009a }
            r9.copyStream(r2, r5)     // Catch:{ all -> 0x009a }
            if (r2 == 0) goto L_0x0045
            r2.close()
            goto L_0x0045
        L_0x0093:
            r5 = move-exception
        L_0x0094:
            if (r1 == 0) goto L_0x0099
            r1.close()
        L_0x0099:
            throw r5
        L_0x009a:
            r5 = move-exception
            r1 = r2
            goto L_0x0094
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.p005io.TiFile.writeFromUrl(java.lang.String, boolean):void");
    }

    public void write(String data, boolean append) throws IOException {
        Log.m29d(TAG, "write called for file = " + this.file, Log.DEBUG_MODE);
        if (!this.stream) {
            try {
                open(append ? 2 : 1, false);
                this.outwriter.write(data);
            } finally {
                close();
            }
        } else if (!this.opened) {
            throw new IOException("Must open before calling write");
        } else if (this.binary) {
            this.outstream.write(data.getBytes());
        } else {
            this.outwriter.write(data);
        }
    }

    public void writeLine(String data) throws IOException {
        if (!this.opened) {
            throw new IOException("Must open before calling readLine");
        } else if (this.binary) {
            throw new IOException("File opened in binary mode, writeLine not available.");
        } else {
            this.outwriter.write(data);
            this.outwriter.write("\n");
        }
    }
}
