package org.appcelerator.titanium;

import android.content.ContextWrapper;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.p005io.TiFile;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper2;
import org.appcelerator.titanium.util.TiUrl;
import p006ti.modules.titanium.stream.FileStreamProxy;

public class TiFileProxy extends KrollProxy {
    private static final String TAG = "TiFileProxy";
    protected String path;
    protected TiBaseFile tbf;

    public TiFileProxy(String sourceUrl, String[] parts) {
        this(sourceUrl, parts, true);
    }

    public TiFileProxy(String sourceUrl, String[] parts, boolean resolve) {
        String path2;
        this.creationUrl = TiUrl.createProxyUrl(sourceUrl);
        String scheme = "appdata-private://";
        Uri uri = Uri.parse(parts[0]);
        if (uri.getScheme() != null) {
            scheme = uri.getScheme() + ":";
            ArrayList<String> pb = new ArrayList<>();
            int schemeLength = scheme.length();
            if (parts[0].charAt(schemeLength + 1) == '/') {
                String s = parts[0].substring(schemeLength + 2);
                if (s != null && s.length() > 0) {
                    pb.add(s);
                }
            } else {
                pb.add(uri.getPath());
            }
            for (int i = 1; i < parts.length; i++) {
                pb.add(parts[i]);
            }
            path2 = TiFileHelper2.joinSegments((String[]) pb.toArray(new String[pb.size()]));
            if (!path2.startsWith(TiUrl.PARENT_PATH) || !path2.startsWith(TiUrl.PATH_SEPARATOR)) {
                path2 = TiUrl.PATH_SEPARATOR + path2;
            }
            pb.clear();
        } else {
            path2 = TiFileHelper2.joinSegments(parts);
        }
        if (resolve) {
            path2 = resolveUrl(scheme, path2);
        }
        this.tbf = TiFileFactory.createTitaniumFile(new String[]{path2}, false);
    }

    public TiFileProxy(TiBaseFile tbf2) {
        this.tbf = tbf2;
    }

    public static <T> String join(Collection<T> objs, String delimiter) {
        if (objs == null || objs.isEmpty()) {
            return "";
        }
        Iterator<T> iter = objs.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuffer buffer = new StringBuffer(String.valueOf(iter.next()));
        while (iter.hasNext()) {
            buffer.append(delimiter).append(String.valueOf(iter.next()));
        }
        return buffer.toString();
    }

    public TiBaseFile getBaseFile() {
        return this.tbf;
    }

    public boolean isFile() {
        return this.tbf.isFile();
    }

    public boolean isDirectory() {
        return this.tbf.isDirectory();
    }

    public boolean getReadonly() {
        return this.tbf.isReadonly();
    }

    public boolean getWritable() {
        return this.tbf.isWriteable();
    }

    public boolean append(Object data) {
        return write(new Object[]{data, Boolean.valueOf(true)});
    }

    public boolean copy(String destination) throws IOException {
        return this.tbf.copy(destination);
    }

    public boolean createDirectory(@argument(optional = true) Object arg) {
        boolean recursive = true;
        if (arg != null) {
            recursive = TiConvert.toBoolean(arg);
        }
        return this.tbf.createDirectory(recursive);
    }

    public boolean createFile() {
        this.tbf = new TiFile(new File(new ContextWrapper(TiApplication.getInstance().getApplicationContext()).getDir(TiC.PROPERTY_DATA, 0) + TiUrl.PATH_SEPARATOR + this.tbf.getNativeFile().getName()), this.path, getExecutable());
        return this.tbf.createFile();
    }

    public boolean deleteDirectory(@argument(optional = true) Object arg) {
        boolean recursive = false;
        if (arg != null) {
            recursive = TiConvert.toBoolean(arg);
        }
        return this.tbf.deleteDirectory(recursive);
    }

    public boolean deleteFile() {
        return this.tbf.deleteFile();
    }

    public boolean exists() {
        return this.tbf.exists();
    }

    public String extension() {
        return this.tbf.extension();
    }

    public boolean getSymbolicLink() {
        return this.tbf.isSymbolicLink();
    }

    public boolean getExecutable() {
        return this.tbf.isExecutable();
    }

    public boolean getHidden() {
        return this.tbf.isHidden();
    }

    public String[] getDirectoryListing() {
        List<String> dl = this.tbf.getDirectoryListing();
        if (dl != null) {
            return (String[]) dl.toArray(new String[0]);
        }
        return null;
    }

    public TiFileProxy getParent() {
        TiBaseFile bf = this.tbf.getParent();
        if (bf != null) {
            return new TiFileProxy(bf);
        }
        return null;
    }

    public boolean move(String destination) throws IOException {
        return this.tbf.move(destination);
    }

    public String getName() {
        return this.tbf.name();
    }

    public String getNativePath() {
        return this.tbf.nativePath();
    }

    public TiBlob read() throws IOException {
        return this.tbf.read();
    }

    public String readLine() throws IOException {
        return this.tbf.readLine();
    }

    public boolean rename(String destination) {
        return this.tbf.rename(destination);
    }

    public String resolve() {
        return getNativePath();
    }

    public double getSize() {
        return (double) this.tbf.size();
    }

    public double spaceAvailable() {
        return this.tbf.spaceAvailable();
    }

    public boolean write(Object[] args) {
        if (args != null) {
            try {
                if (args.length > 0) {
                    boolean append = false;
                    if (args.length > 1 && (args[1] instanceof Boolean)) {
                        append = args[1].booleanValue();
                    }
                    if (args[0] instanceof TiBlob) {
                        ((TiFile) this.tbf).write(args[0], append);
                    } else if (args[0] instanceof String) {
                        ((TiFile) this.tbf).write(args[0], append);
                    } else if (args[0] instanceof TiFileProxy) {
                        ((TiFile) this.tbf).write(args[0].read(), append);
                    } else {
                        Log.m36i(TAG, "Unable to write to an unrecognized file type");
                        return false;
                    }
                    return true;
                }
            } catch (IOException e) {
                Log.m34e(TAG, "IOException encountered", (Throwable) e);
                return false;
            }
        }
        return false;
    }

    public void writeLine(String data) throws IOException {
        this.tbf.writeLine(data);
    }

    public double createTimestamp() {
        return this.tbf.createTimestamp();
    }

    public double modificationTimestamp() {
        return this.tbf.modificationTimestamp();
    }

    public FileStreamProxy open(int mode) throws IOException {
        if (!this.tbf.isOpen()) {
            this.tbf.open(mode, true);
        }
        return new FileStreamProxy(this);
    }

    public InputStream getInputStream() throws IOException {
        return getBaseFile().getInputStream();
    }

    public String toString() {
        return "[object TiFileProxy]";
    }
}
