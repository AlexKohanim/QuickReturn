package org.appcelerator.titanium.p005io;

import android.content.Context;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiFileHelper2;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: org.appcelerator.titanium.io.TiResourceFile */
public class TiResourceFile extends TiBaseFile {
    private static final String TAG = "TiResourceFile";
    private final String path;
    private boolean typeFetched = false;

    public TiResourceFile(String path2) {
        super(2);
        this.path = path2;
    }

    public boolean isDirectory() {
        if (this.typeFetched) {
            return this.typeDir;
        }
        fetchType();
        return this.typeDir;
    }

    public boolean isFile() {
        if (this.typeFetched) {
            return this.typeFile;
        }
        fetchType();
        return this.typeFile;
    }

    public TiBaseFile resolve() {
        return this;
    }

    public InputStream getInputStream() throws IOException {
        Context context = TiApplication.getInstance();
        if (context == null) {
            return null;
        }
        return context.getAssets().open(TiFileHelper2.joinSegments("Resources", this.path));
    }

    public OutputStream getOutputStream() {
        return null;
    }

    public File getNativeFile() {
        return new File(toURL());
    }

    public void write(String data, boolean append) throws IOException {
        throw new IOException("read only");
    }

    public void open(int mode, boolean binary) throws IOException {
        if (mode == 0) {
            InputStream in = getInputStream();
            if (in != null) {
                if (binary) {
                    this.instream = new BufferedInputStream(in);
                } else {
                    this.inreader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                }
                this.opened = true;
                return;
            }
            throw new FileNotFoundException("File does not exist: " + this.path);
        }
        throw new IOException("Resource file may not be written.");
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

    public boolean exists() {
        boolean result;
        boolean result2 = false;
        InputStream is = null;
        try {
            is = getInputStream();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (!getDirectoryListing().isEmpty()) {
                result2 = true;
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        }
        return result;
    }

    public String name() {
        int idx = this.path.lastIndexOf(TiUrl.PATH_SEPARATOR);
        if (idx != -1) {
            return this.path.substring(idx + 1);
        }
        return this.path;
    }

    public String extension() {
        int idx = this.path.lastIndexOf(TiUrl.CURRENT_PATH);
        if (idx != -1) {
            return this.path.substring(idx + 1);
        }
        return null;
    }

    public String nativePath() {
        return toURL();
    }

    public double spaceAvailable() {
        return 0.0d;
    }

    public String toURL() {
        return TiC.URL_ANDROID_ASSET_RESOURCES + this.path;
    }

    public long size() {
        long length = 0;
        InputStream is = null;
        try {
            is = getInputStream();
            length = (long) is.available();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.m47w(TAG, e.getMessage(), e, Log.DEBUG_MODE);
                }
            }
        } catch (IOException e2) {
            Log.m46w(TAG, "Error while trying to determine file size: " + e2.getMessage(), (Throwable) e2);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                    Log.m47w(TAG, e3.getMessage(), e3, Log.DEBUG_MODE);
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                    Log.m47w(TAG, e4.getMessage(), e4, Log.DEBUG_MODE);
                }
            }
        }
        return length;
    }

    public List<String> getDirectoryListing() {
        List<String> listing = new ArrayList<>();
        try {
            String lpath = TiFileHelper2.joinSegments("Resources", this.path);
            if (lpath.endsWith(TiUrl.PATH_SEPARATOR)) {
                lpath = lpath.substring(0, lpath.lastIndexOf(TiUrl.PATH_SEPARATOR));
            }
            String[] names = TiApplication.getInstance().getAssets().list(lpath);
            if (names != null) {
                for (String add : names) {
                    listing.add(add);
                }
            }
        } catch (IOException e) {
            Log.m34e(TAG, "Error while getting a directory listing: " + e.getMessage(), (Throwable) e);
        }
        return listing;
    }

    public String toString() {
        return toURL();
    }

    private void fetchType() {
        InputStream is = null;
        try {
            is = getInputStream();
            this.typeDir = false;
            this.typeFile = true;
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            this.typeDir = true;
            this.typeFile = false;
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        }
        this.typeFetched = true;
    }
}
